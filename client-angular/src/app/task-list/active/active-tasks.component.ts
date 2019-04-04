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

import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

import {TaskService} from 'app/task-service/task.service';
import {TaskItem, TaskPriority, TaskStatus} from 'proto/todolist/q/projections_pb';
import {TaskListComponent} from 'app/task-list/task-list.component';

/**
 * A component displaying active tasks, i.e. those which are not completed, deleted, or in draft
 * state.
 */
@Component({
  selector: 'app-active-tasks',
  templateUrl: './active-tasks.component.html',
  styleUrls: ['./active-tasks.component.css']
})
export class ActiveTasksComponent implements OnInit {

  constructor(private readonly taskService: TaskService,
              private formBuilder: FormBuilder,
              private readonly changeDetector: ChangeDetectorRef) {
    this.createBasicTaskForms = formBuilder.group({
      taskDescription: ['', Validators.pattern('(.*?[a-zA-Z0-9]){3,}.*')]
    });
  }

  private createBasicTaskForms: FormGroup;

  private displayUrgent = false;
  private displayNormal = false;
  private displayLow = false;

  @ViewChild('urgentList')
  urgentList: TaskListComponent;

  @ViewChild('normalList')
  normalList: TaskListComponent;

  @ViewChild('lowList')
  lowList: TaskListComponent;


  private readonly activeFilter: (t: TaskItem) => boolean =
    (taskItem) => taskItem.getStatus() === TaskStatus.OPEN || taskItem.getStatus() === TaskStatus.FINALIZED

  private readonly urgentFilter: (t: TaskItem) => boolean =
    (taskItem) => this.activeFilter(taskItem) && taskItem.getPriority() === TaskPriority.HIGH

  private readonly lowPriorityFilter: (t: TaskItem) => boolean =
    (taskItem) => this.activeFilter(taskItem) && taskItem.getPriority() === TaskPriority.LOW

  private readonly normalPriorityFilter: (t: TaskItem) => boolean =
    (taskItem) => this.activeFilter(taskItem) && taskItem.getPriority() === TaskPriority.TP_UNDEFINED

  /**
   * Sends a command to create a basic task, i.e. a task without label, due date, and with a
   * `Normal` priority.
   *
   * See `commands.proto#CreateBasicTask` in the `model` module.
   * @param taskDescription desired description of the task.
   */
  private createBasicTask(taskDescription: string): void {
    this.taskService.createBasicTask(taskDescription);
  }

  ngOnInit(): void {
    this.urgentList.hasElements$.subscribe(hasItems => {
      this.displayUrgent = hasItems;
    });
    this.normalList.hasElements$.subscribe(hasItems => {
      this.displayNormal = hasItems;
    });
    this.lowList.hasElements$.subscribe(hasItems => {
      this.displayLow = hasItems;
    });
    this.changeDetector.detectChanges();
  }
}
