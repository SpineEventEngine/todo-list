/*
 * Copyright 2017, TeamDev Ltd. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.server.firebase;

import io.spine.core.Subscribe;
import io.spine.core.TenantId;
import io.spine.server.event.EventSubscriber;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static com.google.common.collect.Sets.newConcurrentHashSet;

/**
 * @author Dmytro Dashenkov
 */
final class TenantEventSubscriber extends EventSubscriber {

    private final Lock lock = new ReentrantLock(false);

    private final Set<TenantId> acknowledgedTenants = newConcurrentHashSet();
    private final Consumer<TenantId> tenantCallback;

    TenantEventSubscriber(Consumer<TenantId> tenantCallback) {
        this.tenantCallback = tenantCallback;
    }

    @Subscribe(external = true)
    public void on(TenantAdded event) {
        lock.lock();
        try {
            final TenantId id = event.getTenantId();
            addNewTenant(id);
        } finally {
            lock.unlock();
        }
    }

    private void addNewTenant(TenantId tenantId) {
        if (!acknowledgedTenants.contains(tenantId)) {
            acknowledgedTenants.add(tenantId);
            tenantCallback.accept(tenantId);
        }
    }
}