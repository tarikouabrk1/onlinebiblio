package com.library.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class PrometheusMetricsServlet extends HttpServlet {

    private static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    // Business Metrics
    private static final Counter loginAttempts = Counter.builder("library_login_attempts_total")
            .description("Total number of login attempts")
            .register(registry);

    private static final Counter successfulLogins = Counter.builder("library_successful_logins_total")
            .description("Total number of successful logins")
            .register(registry);

    private static final Counter booksBorrowed = Counter.builder("library_books_borrowed_total")
            .description("Total number of books borrowed")
            .register(registry);

    private static final Counter booksReturned = Counter.builder("library_books_returned_total")
            .description("Total number of books returned")
            .register(registry);

    private static final AtomicInteger activeBorrowings = new AtomicInteger(0);

    static {
        // Gauge for active borrowings
        Gauge.builder("library_active_borrowings", activeBorrowings, AtomicInteger::get)
                .description("Current number of active borrowings")
                .register(registry);

        // JVM Metrics
        registry.config().commonTags("application", "online-library");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String metrics = registry.scrape();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain; version=0.0.4; charset=utf-8");
        resp.getWriter().write(metrics);
    }

    public static PrometheusMeterRegistry getRegistry() {
        return registry;
    }

    // Helper methods to increment counters from other servlets
    public static void incrementLoginAttempts() {
        loginAttempts.increment();
    }

    public static void incrementSuccessfulLogins() {
        successfulLogins.increment();
    }

    public static void incrementBooksBorrowed() {
        booksBorrowed.increment();
    }

    public static void incrementBooksReturned() {
        booksReturned.increment();
    }

    public static void setActiveBorrowings(int count) {
        activeBorrowings.set(count);
    }
}