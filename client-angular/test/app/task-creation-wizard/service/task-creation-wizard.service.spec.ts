/*
 * Copyright 2019, TeamDev. All rights reserved.
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

import {TestBed} from '@angular/core/testing';

import {Client} from 'spine-web';

import {TaskCreationWizard} from '../../../../src/app/task-creation-wizard/service/task-creation-wizard.service';
import {TaskService} from '../../../../src/app/task-service/task.service';
import {mockSpineWebClient, subscriptionDataOf} from '../../given/mock-spine-web-client';
import {houseTask, houseTasks} from '../../given/tasks';

import {Timestamp} from 'google-protobuf/google/protobuf/timestamp_pb';
import {TaskPriority} from 'generated/main/js/todolist/attributes_pb';
import {TaskCreationId, TaskId} from 'generated/main/js/todolist/identifiers_pb';
import {TaskCreation} from 'generated/main/js/todolist/model_pb';
import {TaskDescription} from 'generated/main/js/todolist/values_pb';
import {
  AddLabels,
  CancelTaskCreation,
  CompleteTaskCreation,
  SkipLabels,
  StartTaskCreation,
  UpdateTaskDetails
} from 'generated/main/js/todolist/c/commands_pb';
import {TaskView} from 'generated/main/js/todolist/q/projections_pb';

describe('TaskCreationWizard', () => {

  class Given {

    /**
     * Initializes wizard with some basic task data.
     */
    static initializedWizard(): Promise<void> {
      const creationProcess = Given.taskCreationProcess();
      const task = Given.task();
      const provideMockData = (type, id, resolveCallback) => {
        if (type.class() === TaskCreation) {
          resolveCallback(creationProcess);
        } else if (type.class() === TaskView) {
          resolveCallback(task);
        }
      };
      mockClient.fetchById.and.callFake(provideMockData);
      return wizard.init(creationProcess.getId().getValue());
    }

    static taskCreationProcess(): TaskCreation {
      const creationIdValue = 'task-creation-ID';
      const taskCreationId = new TaskCreationId();
      taskCreationId.setValue(creationIdValue);
      const stage = TaskCreation.Stage.LABEL_ASSIGNMENT;
      const taskCreation = new TaskCreation();
      taskCreation.setId(taskCreationId);
      taskCreation.setStage(stage);
      taskCreation.setTaskId(Given.task().getId());
      return taskCreation;
    }

    static task(): TaskView {
      return houseTask();
    }

    static newDescription(): TaskDescription {
      const newDescription = new TaskDescription();
      newDescription.setValue('New description');
      return newDescription;
    }

    static newPriority(): TaskPriority {
      return TaskPriority.NORMAL;
    }

    static newDueDate(): Timestamp {
      const date = new Date();
      date.setHours(date.getHours() + 10);
      return Timestamp.fromDate(date);
    }
  }

  const mockClient = mockSpineWebClient();
  const unsubscribe = jasmine.createSpy('unsubscribe');
  mockClient.subscribeToEntities.and.returnValue(subscriptionDataOf(
    [houseTasks()], [], [], unsubscribe
  ));

  let wizard: TaskCreationWizard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TaskCreationWizard,
        TaskService,
        {provide: Client, useValue: mockClient}
      ]
    });
    wizard = TestBed.get(TaskCreationWizard);
  });

  it('should be created', () => {
    expect(wizard).toBeTruthy();
  });

  it('should initialize the process from scratch', () => {
    mockClient.sendCommand.and.callFake((command, resolveCallback) => resolveCallback());
    wizard.init()
      .then(() => {
        const calls = mockClient.sendCommand.calls;
        expect(calls.count()).toEqual(1);
        const callArgs = calls.argsFor(0);
        const command = callArgs[0];
        expect(wizard.id).toEqual(command.getId());
        expect(wizard.taskId).toEqual(command.getTaskId());
        expect(wizard.stage).toEqual(TaskCreation.Stage.TASK_DEFINITION);
      })
      .catch(err =>
        fail(`'StartTaskCreation' command rejected: ${err}`)
      );
  });

  it('should restore existing creation process by ID', () => {
    const creationProcess = Given.taskCreationProcess();
    const task = Given.task();
    Given.initializedWizard()
      .then(() => {
        expect(wizard.id).toEqual(creationProcess.getId());
        expect(wizard.taskId).toEqual(task.getId());
        expect(wizard.stage).toEqual(creationProcess.getStage());
        expect(wizard.taskDescription).toEqual(task.getDescription());
        expect(wizard.taskPriority).toEqual(task.getPriority());
        expect(wizard.taskDueDate).toEqual(task.getDueDate());
        expect(wizard.taskLabels).toEqual([]);
      })
      .catch(err =>
        fail(`Task restoration failed: ${err}`)
      );
  });

  it('should produce an error if nothing is found by the specified ID', () => {
    mockClient.fetchById.and.callFake((type, id, resolve) => resolve(null));
    const theId = 'some-ID';
    wizard.init(theId)
      .then(() => {
        fail('Task restoration should have been rejected');
      })
      .catch(err =>
        expect(err).toEqual(`No task creation process found for ID: ${theId}`)
      );
  });

  it('should propagate `init` errors as Promise rejection', () => {
    const errorMessage = 'Could not start the task creation process';
    mockClient.sendCommand.and.callFake((cmd, resolve, reject) => reject(errorMessage));
    wizard.init()
      .then(() => {
        fail('Task restoration should have been rejected');
      })
      .catch(err =>
        expect(err).toEqual(errorMessage)
      );
  });

  it('should update task details', () => {
    Given.initializedWizard()
      .then(() => {
        updateDetailsAndCheck(Given.newDescription(), Given.newPriority(), Given.newDueDate());
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  it('should not set previous description value if it is undefined', () => {
    Given.initializedWizard()
      .then(() => {
        wizard.taskDescription = undefined;
        updateDetailsAndCheck(Given.newDescription(), Given.newPriority(), Given.newDueDate());
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  it('should produce an error when new description is undefined', () => {
    Given.initializedWizard()
      .then(() => {
        wizard.updateTaskDetails(undefined, Given.newPriority(), Given.newDueDate())
          .then(() => fail('Details update should have failed'))
          .catch(err => expect(err).toEqual('Description value must be set.'));
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  it('should ignore description change if specified description is same as previous one', () => {
    Given.initializedWizard()
      .then(() => {
        updateDetailsAndCheck(wizard.taskDescription, Given.newPriority(), Given.newDueDate());
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  it('should not set previous priority value if it is undefined', () => {
    Given.initializedWizard()
      .then(() => {
        wizard.taskPriority = undefined;
        updateDetailsAndCheck(Given.newDescription(), Given.newPriority(), Given.newDueDate());
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  it('should ignore priority change if specified priority is same as previous one', () => {
    Given.initializedWizard()
      .then(() => {
        updateDetailsAndCheck(Given.newDescription(), wizard.taskPriority, Given.newDueDate());
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  it('should not set previous due date value if it is undefined', () => {
    Given.initializedWizard()
      .then(() => {
        wizard.taskDueDate = undefined;
        updateDetailsAndCheck(Given.newDescription(), Given.newPriority(), Given.newDueDate());
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  it('should ignore due date change if specified due date is same as previous one', () => {
    Given.initializedWizard()
      .then(() => {
        updateDetailsAndCheck(Given.newDescription(), Given.newPriority(), wizard.taskDueDate);
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  it('should produce an error if task due date is before current time', () => {
    Given.initializedWizard()
      .then(() => {
        const dueDate = new Date();
        dueDate.setHours(dueDate.getHours() - 10);
        wizard.updateTaskDetails(Given.newDescription(), Given.newPriority(), dueDate)
          .then(() => fail('Details update should have failed'))
          .catch(err => expect(err).toEqual(
            `Task due date before current time is not allowed, specified date: ${dueDate}`
          ));
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  it('should forward errors during task details update as Promise rejection', () => {
    Given.initializedWizard()
      .then(() => {
        const errorMessage = 'Updating task details failed';
        mockClient.sendCommand.and.callFake((cmd, resolve, reject) => reject(errorMessage));
        wizard.updateTaskDetails(Given.newDescription(), Given.newPriority(), Given.newDueDate())
          .then(() => fail('Details update should have failed'))
          .catch(err => expect(err).toEqual(errorMessage));
      })
      .catch(err => fail(`Wizard init failed: ${err}`));
  });

  function updateDetailsAndCheck(newDescription: TaskDescription,
                                 newPriority: TaskPriority,
                                 newDueDate: Timestamp) {
    mockClient.sendCommand.and.callFake((cmd, resolve) => resolve());

    const oldDescription = wizard.taskDescription;
    const oldPriority = wizard.taskPriority;
    const oldDueDate = wizard.taskDueDate;

    wizard.updateTaskDetails(newDescription, newPriority, newDueDate)
      .then(() => {
        if (newDescription) {
          expect(wizard.taskDescription).toEqual(newDescription);
        } else {
          expect(wizard.taskDescription).toEqual(oldDescription);
        }
        if (newPriority) {
          expect(wizard.taskPriority).toEqual(newPriority);
        } else {
          expect(wizard.taskPriority).toEqual(oldPriority);
        }
        if (newDueDate) {
          expect(wizard.taskDueDate).toEqual(newDueDate);
        } else {
          expect(wizard.taskDueDate).toEqual(oldDueDate);
        }
      })
      .catch(err => fail(`Updating task details failed: ${err}`));
  }
});
