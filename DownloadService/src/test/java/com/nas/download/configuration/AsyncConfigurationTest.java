package com.nas.download.configuration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AsyncConfigurationTest {

    @Test
    void getAsyncExecutorTest() {
        assertNotNull(new AsyncConfiguration().getAsyncExecutor());
    }

    @Test
    void getAsyncUncaughtExceptionHandlerTest() {
        assertNotNull(new AsyncConfiguration().getAsyncUncaughtExceptionHandler());
    }

    @Test
    void webMvcConfigurerConfigurerTest() {
        AsyncTaskExecutor asyncTaskExecutor = Mockito.mock(AsyncTaskExecutor.class);
        CallableProcessingInterceptor callableProcessingInterceptor = Mockito.mock(CallableProcessingInterceptor.class);

        assertNotNull(new AsyncConfiguration().webMvcConfigurerConfigurer(asyncTaskExecutor, callableProcessingInterceptor));

        WebMvcConfigurer webMvcConfigurer = new AsyncConfiguration().webMvcConfigurerConfigurer(asyncTaskExecutor, callableProcessingInterceptor);
        AsyncSupportConfigurer asyncSupportConfigurer = Mockito.mock(AsyncSupportConfigurer.class);
        Mockito.when(asyncSupportConfigurer.setDefaultTimeout(Mockito.anyLong())).thenReturn(asyncSupportConfigurer);
        Mockito.when(asyncSupportConfigurer.setTaskExecutor(Mockito.any())).thenReturn(asyncSupportConfigurer);
        Mockito.when(asyncSupportConfigurer.registerCallableInterceptors(Mockito.any())).thenReturn(asyncSupportConfigurer);

        webMvcConfigurer.configureAsyncSupport(asyncSupportConfigurer);
    }

    @Test
    @SneakyThrows
    void callableProcessingInterceptorTest() {
        NativeWebRequest nativeWebRequest = Mockito.mock(NativeWebRequest.class);
        Callable<String> callable = Mockito.mock(Callable.class);

        assertNotNull(new AsyncConfiguration().callableProcessingInterceptor());

        CallableProcessingInterceptor callableProcessingInterceptor = new AsyncConfiguration().callableProcessingInterceptor();
        callableProcessingInterceptor.handleTimeout(nativeWebRequest, callable);
    }
}
