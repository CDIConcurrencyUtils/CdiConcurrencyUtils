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
package cdi.concurrency.concurrencyutils.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
@Lock
@Interceptor
public class LockInterceptor {

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

  @AroundInvoke
  public Object concurrencyControl(InvocationContext ctx) throws Exception {
    Lock lockAnnotation = ctx.getMethod().getAnnotation(Lock.class);

    if (lockAnnotation == null) {
      lockAnnotation = ctx.getTarget().getClass().getAnnotation(Lock.class);
    }

    Object returnValue = null;
    switch (lockAnnotation.value()) {
      case WRITE:
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        try {
          writeLock.lock();
          returnValue = ctx.proceed();
        } finally {
          writeLock.unlock();
        }
        break;
      case READ:
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try {
          readLock.lock();
          returnValue = ctx.proceed();
        } finally {
          readLock.unlock();
        }
        break;
    }
    return returnValue;
  }
}
