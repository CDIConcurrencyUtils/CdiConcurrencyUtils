/*
 * Copyright 2016 Stephan Knitelius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cdi.concurrencyutils.utils.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
@CDIAsynchronous
@Interceptor
public class CDIAsyncInterceptor {

  @Resource
  private ExecutorService executerService;

  @AroundInvoke
  public Object intercept(final InvocationContext ic) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        // Unpack the "fake" future from the business method.
        Future<?> proceed = (Future<?>) ic.proceed();
        return proceed.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }, executerService);
  }
}
