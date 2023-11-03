/*
 * Copyright 2023 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.plugin.resilience4j.interceptor;

import com.navercorp.pinpoint.bootstrap.context.AsyncContext;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AsyncContextSpanEventSimpleAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.plugin.reactor.ReactorContextAccessorUtils;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.util.ArrayArgumentUtils;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.plugin.resilience4j.Resilience4JConstants;
import com.navercorp.pinpoint.plugin.resilience4j.Resilience4JPluginConfig;

public class CircuitBreakerSubscriberInterceptor extends AsyncContextSpanEventSimpleAroundInterceptor {
    private final boolean traceCircuitBreaker;

    public CircuitBreakerSubscriberInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor) {
        super(traceContext, methodDescriptor);
        final Resilience4JPluginConfig config = new Resilience4JPluginConfig(traceContext.getProfilerConfig());
        this.traceCircuitBreaker = config.isTraceCircuitBreaker();
    }

    public AsyncContext getAsyncContext(Object target, Object[] args) {
        if (traceCircuitBreaker) {
            return ReactorContextAccessorUtils.getAsyncContext(target);
        }
        return null;
    }

    @Override
    public void doInBeforeTrace(SpanEventRecorder recorder, AsyncContext asyncContext, Object target, Object[] args) {
    }

    public AsyncContext getAsyncContext(Object target, Object[] args, Object result, Throwable throwable) {
        if (traceCircuitBreaker) {
            return ReactorContextAccessorUtils.getAsyncContext(target);
        }
        return null;
    }

    @Override
    public void afterTrace(AsyncContext asyncContext, Trace trace, SpanEventRecorder recorder, Object target, Object[] args, Object result, Throwable throwable) {
        if (traceCircuitBreaker && trace.canSampled()) {
            recorder.recordApi(methodDescriptor);
            recorder.recordServiceType(Resilience4JConstants.RESILIENCE4J);
            recorder.recordException(throwable);

            final Throwable argThrowable = ArrayArgumentUtils.getArgument(args, 0, Throwable.class);
            if (argThrowable != null) {
                final String message = argThrowable.getMessage();
                if (StringUtils.hasLength(message)) {
                    final String hookOnMessage = "CIRCUIT BREAKER(" + StringUtils.abbreviate(message, 128) + ")";
                    recorder.recordAttribute(AnnotationKey.ARGS0, hookOnMessage);
                } else {
                    recorder.recordAttribute(AnnotationKey.ARGS0, "CIRCUIT BREAKER");
                }
            }
        }
    }

    @Override
    public void doInAfterTrace(SpanEventRecorder recorder, Object target, Object[] args, Object result, Throwable throwable) {
    }
}
