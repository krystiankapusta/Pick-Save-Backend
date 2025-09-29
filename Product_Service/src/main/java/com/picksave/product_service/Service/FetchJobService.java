package com.picksave.product_service.Service;

import com.picksave.product_service.Model.FetchJob;
import com.picksave.product_service.Model.FetchJobStatus;
import com.picksave.product_service.Repository.FetchJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.*;


@Service
public class FetchJobService {

    private final FetchJobRepository fetchJobRepository;
    private final ExternalProductService externalProductService;
    private final ThreadPoolTaskExecutor fetchExecutor;
    private final Map<String, Future<?>> runningJobs = new ConcurrentHashMap<>();
    private final Map<String, Object> shopLocks = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(FetchJobService.class);


    public FetchJobService(FetchJobRepository fetchJobRepository, ExternalProductService externalProductService, @Qualifier("fetchExecutor") ThreadPoolTaskExecutor fetchExecutor) {
        this.fetchJobRepository = fetchJobRepository;
        this.externalProductService = externalProductService;
        this.fetchExecutor = fetchExecutor;
    }

    public FetchJob startJob(String shopName) {
        Object lock = shopLocks.computeIfAbsent(shopName, k -> new Object());
        synchronized (lock) {
            FetchJob job = fetchJobRepository.findByShopName(shopName).orElse(null);

            if (job != null && job.getFetchJobStatus() == FetchJobStatus.RUNNING) {
                logger.info("Job for shop {} is already RUNNING", shopName);
                return job;
            }

            if (job == null) {
                job = new FetchJob(shopName, 0, FetchJobStatus.RUNNING);
            } else {
                job.setFetchJobStatus(FetchJobStatus.RUNNING);
            }
            job = fetchJobRepository.save(job);
            logger.info("Starting job for shop {} (lastPage={})", shopName, job.getLastPage());

            Future<?> future = fetchExecutor.submit(() -> {
                Thread current = Thread.currentThread();
                String threadName = current.getName();
                logger.info("Worker thread {} started for shop {}", threadName, shopName);
                try {
                    externalProductService.doFetch(shopName);
                } catch (Exception e) {
                    logger.error("Unhandled exception in fetch job for shop " + shopName, e);
                    try {
                        FetchJob j = fetchJobRepository.findByShopName(shopName).orElse(null);
                        if (j != null) {
                            j.setFetchJobStatus(FetchJobStatus.STOPPED);
                            fetchJobRepository.save(j);
                        }
                    } catch (Exception ex) {
                        logger.warn("Failed to set STOPPED status after exception for shop {}", shopName, ex);
                    }
                } finally {
                    runningJobs.remove(shopName);
                    logger.info("Worker thread {} finished for shop {}", threadName, shopName);
                }
            });

            runningJobs.put(shopName, future);
            return job;
        }
    }

    public FetchJob stopJob(String shopName) {
        Object lock = shopLocks.computeIfAbsent(shopName, k -> new Object());
        synchronized (lock) {
            FetchJob job = fetchJobRepository.findByShopName(shopName)
                    .orElseThrow(() -> new RuntimeException("Job not found for shop: " + shopName));

            job.setFetchJobStatus(FetchJobStatus.STOPPED);
            fetchJobRepository.save(job);
            logger.info("STOP requested for shop {}, setting DB status STOPPED", shopName);

            Future<?> future = runningJobs.remove(shopName);
            if (future != null) {
                logger.info("Cancelling Future for shop {}", shopName);
                future.cancel(true);
                try {
                    future.get(5, TimeUnit.SECONDS);
                } catch (CancellationException | InterruptedException e) {
                    logger.info("Future for shop {} cancelled/interrupt acknowledged", shopName);
                } catch (ExecutionException ee) {
                    logger.warn("Worker for shop {} ended with exception: {}", shopName, ee.getMessage());
                } catch (TimeoutException te) {
                    logger.warn("Worker for shop {} didn't finish within timeout after cancel()", shopName);
                }
            } else {
                logger.info("No active Future found for shop {}", shopName);
            }
            return job;
        }
    }

    public FetchJob resumeJob(String shopName) {
        Object lock = shopLocks.computeIfAbsent(shopName, k -> new Object());
        synchronized (lock) {
            FetchJob job = fetchJobRepository.findByShopName(shopName)
                    .orElseThrow(() -> new RuntimeException("Job not found for shop: " + shopName));

            if (job.getFetchJobStatus() == FetchJobStatus.STOPPED) {
                job.setFetchJobStatus(FetchJobStatus.RUNNING);
                job = fetchJobRepository.save(job);

                Future<?> future = fetchExecutor.submit(() -> {
                    Thread current = Thread.currentThread();
                    String threadName = current.getName();
                    logger.info("Resumed worker thread {} started for shop {}", threadName, shopName);
                    try {
                        externalProductService.doFetch(shopName);
                    } catch (Exception e) {
                        logger.error("Unhandled exception in resumed fetch job for shop {}", shopName, e);
                    } finally {
                        runningJobs.remove(shopName);
                        logger.info("Resumed worker thread {} finished for shop {}", threadName, shopName);
                    }
                });

                runningJobs.put(shopName, future);
            } else {
                logger.info("Job for shop {} is not STOPPED, current status: {}", shopName, job.getFetchJobStatus());
            }
            return job;
        }
    }


    public FetchJob completeJob(String shopName) {
        Object lock = shopLocks.computeIfAbsent(shopName, k -> new Object());
        synchronized (lock) {
            FetchJob job = fetchJobRepository.findByShopName(shopName)
                    .orElseThrow(() -> new RuntimeException("Job not found for shop: " + shopName));

            Future<?> future = runningJobs.remove(shopName);
            if (future != null) {
                logger.info("Cancelling and completing Future for shop {}", shopName);
                future.cancel(true);
                try {
                    future.get(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    logger.warn("Worker for shop {} did not terminate cleanly: {}", shopName, e.getMessage());
                }
            }

            job.setFetchJobStatus(FetchJobStatus.COMPLETED);
            job.setLastPage(0);
            return fetchJobRepository.save(job);
        }
    }

}
