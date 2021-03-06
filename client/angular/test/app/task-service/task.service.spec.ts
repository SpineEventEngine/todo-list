/*
 * Copyright 2021, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {Client, Message} from 'spine-web';

import {TaskService} from 'app/task-service/task.service';
import {
    mockSpineWebClient,
    observableSubscriptionDataOf,
    subscriptionDataOf
} from 'test/given/mock-spine-web-client';
import {chore, chore2, CHORE_1_DESC, CHORE_1_ID, CHORE_2_DESC, CHORE_2_ID} from 'test/given/tasks';
import {BehaviorSubject} from 'rxjs';
import {TaskStatus, TaskView} from 'proto/todolist/views_pb';
import {mockNotificationService} from 'test/given/layout-service';
import {NotificationService} from 'app/layout/notification.service';

describe('TaskService', () => {
  const mockClient = mockSpineWebClient();
  const unsubscribe = jasmine.createSpy();
  const notificationService = mockNotificationService();

  const addedTasksSubject = new BehaviorSubject<TaskView>(chore());

  function makeCommandFail() {
    mockClient.sendCommand.and.callFake((cmd: Message, onSuccess: () => void, onError: (err) => void) => {
      const error = {
        assuresCommandNeglected: () => true
      };
      onError(error);
      tick();
    });
  }

  mockClient.subscribe.and.returnValue(observableSubscriptionDataOf(
      addedTasksSubject.asObservable(), unsubscribe
  ));
  mockClient.fetch.and.returnValue(Promise.resolve([]));

  let service: TaskService;
  beforeEach(fakeAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        TaskService, {provide: Client, useValue: mockClient},
        NotificationService, {provide: NotificationService, useValue: notificationService}
      ]
    });
    service = TestBed.get(TaskService);
    // tslint:disable-next-line:no-unused-expression triggers task subscription
    service.tasks;
    tick();
  }));

  afterEach(() => {
    addedTasksSubject.next(chore());

    mockClient.sendCommand.and.callThrough();
    mockClient.subscribe.and.returnValue(observableSubscriptionDataOf(
        addedTasksSubject.asObservable(), unsubscribe
    ));
    mockClient.fetch.and.returnValue(Promise.resolve([]));
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should update the task list without relying on server response', fakeAsync(() => {
    const expectedDescription = 'some task';
    service.createBasicTask(expectedDescription);
    const taskDescriptions = service.tasks.map(task => task.getDescription().getValue());
    expect(taskDescriptions).toContain(expectedDescription);
  }));

  it('should optimistically broadcast added tasks', () => {
    const idToComplete = service.tasks[0].getId();
    service.completeTask(idToComplete);
    const tasks: TaskView[] = service.tasks;
    const firstChore = tasks.find(task => task.getId() === idToComplete);
    expect(firstChore).toBeTruthy();
    expect(firstChore.getStatus()).toBe(TaskStatus.COMPLETED);
  });

  it('should roll optimistic completions back if the command handling fails', fakeAsync(() => {
    const idToComplete = service.tasks[0].getId();
    makeCommandFail();
    service.completeTask(idToComplete);
    tick();
    const noTasksAreCompleted = service.tasks.every(value => value.getStatus() === TaskStatus.OPEN);
    expect(noTasksAreCompleted).toBe(true);
  }));

  it('should update task list with a deleted task without waiting for the server response', () => {
    const idToDelete = service.tasks[0].getId();
    service.deleteTask(idToDelete);
    const tasks: TaskView[] = service.tasks;
    const firstChore = tasks.find(task => task.getId() === idToDelete);
    expect(firstChore).toBeTruthy();
    expect(firstChore.getStatus()).toBe(TaskStatus.DELETED);
  });

  it('should rollback deleted tasks if the deletion command fails', fakeAsync(() => {
    const idToDelete = service.tasks[0].getId();
    makeCommandFail();
    service.deleteTask(idToDelete);
    tick();
    const noneAreDeleted = service.tasks.every(value => value.getStatus() === TaskStatus.OPEN);
    expect(noneAreDeleted).toBe(true);
  }));

  it('should fetch an expected list of tasks', () => {
    service.tasks$.toPromise().then(fetchedTasks => {
      expect(fetchedTasks.length).toBe(2);
      expect(fetchedTasks[0].getId().getUuid()).toBe(CHORE_1_ID);
      expect(fetchedTasks[0].getDescription().getValue()).toBe(CHORE_1_DESC);
      expect(fetchedTasks[1].getId().getUuid()).toBe(CHORE_2_ID);
      expect(fetchedTasks[1].getDescription().getValue()).toBe(CHORE_2_DESC);
    });
  });

  it('should subscribe to the task changes', fakeAsync(() => {
    const task1 = chore();
    const task2 = chore2();

    // Initially, the `task1` is fetched.
    mockClient.fetch.and.returnValue(Promise.resolve([task1]));

    // The `task1` is deleted, the `task2` is added.
    mockClient.subscribe.and.returnValue(subscriptionDataOf(
        [task2], [], [task1], unsubscribe
    ));

    // Check fetched tasks contain only `task2`.
    service.tasks$.toPromise().then(fetchedTasks => {
      expect(fetchedTasks.length).toBe(1);
      expect(fetchedTasks[0].getId().getUuid()).toBe(CHORE_2_ID);
      expect(fetchedTasks[0].getDescription().getValue()).toBe(CHORE_2_DESC);
    }).catch(err =>
        fail(`Task lookup should have been resolved, actually rejected with an error: ${err}`)
    );
  }));

  it('should fetch a single task view by ID', fakeAsync(() => {
    const theTask = chore();
    mockClient.fetch.and.returnValue(Promise.resolve([theTask]));
    service.fetchById(theTask.getId())
           .then(taskView => expect(taskView).toEqual(theTask))
           .catch(err =>
               fail(`Task details should have been resolved, actually rejected with an error: ${err}`)
           );
  }));

  it('should produce an error when no matching task is found during lookup', fakeAsync(() => {
    const taskId = chore().getId();
    mockClient.fetch.and.returnValue(Promise.resolve([]));

    service.fetchById(taskId)
           .then(() => fail('Task details lookup should have been rejected'))
           .catch(err => expect(err).toEqual(`No task view found for ID: ${taskId}`));
  }));

  it('should propagate errors from Spine Web Client on `fetchById`', fakeAsync(() => {
    const errorMessage = 'Task details lookup rejected';
    mockClient.fetch.and.returnValue(Promise.reject(errorMessage));

    service.fetchById(chore().getId())
           .then(() => fail('Task details lookup should have been rejected'))
           .catch(err => expect(err).toEqual(errorMessage));
  }));
});
