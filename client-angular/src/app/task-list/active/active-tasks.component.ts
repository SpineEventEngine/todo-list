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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {ErrorStateMatcher} from '@angular/material/core';
import {FormControl, FormGroup, FormGroupDirective, NgForm, Validators} from '@angular/forms';

import {TaskService} from '../../task-service/task.service';
import {TaskItem} from 'generated/main/js/todolist/q/projections_pb';
import {TaskDescription} from 'generated/main/js/todolist/values_pb';

/**
 * A component displaying active tasks, i.e. those which are not completed, deleted, or in draft
 * state.
 */
@Component({
  selector: 'app-active-tasks',
  templateUrl: './active-tasks.component.html',
  styleUrls: ['./active-tasks.component.css'],

})
export class ActiveTasksComponent implements OnInit, OnDestroy {

  private unsubscribe: () => void;

  /** Visible for testing. */
  readonly tasks: TaskItem[] = [];

  constructor(private readonly taskService: TaskService) {
  }

  descriptionFormControl = new FormControl('', [
    Validators.required,
    ActiveTasksComponent.taskFormValidator
  ]);

  matcher = new ValidTaskDescriptionMatcher();

  taskForms = new FormGroup({
    descriptionFormControl: this.descriptionFormControl
  });

  static taskFormValidator(control: FormControl) {
    const isWhitespaceOnly = (control.value || '').trim().length === 0;
    const isValid = !isWhitespaceOnly;
    return isValid ? null : {whitespace: true};
  }

  createBasicTask(taskDescription: string): void {
    this.taskService.createBasicTask(taskDescription);
  }

  ngOnInit(): void {
    this.taskService.subscribeToActive(this.tasks)
      .then(unsubscribe => this.unsubscribe = unsubscribe);
  }

  ngOnDestroy(): void {
    // TODO:2019-03-12:dmytro.kuzmin: Handle the cases of component being destroyed before the
    // todo subscription process is finished.
    if (this.unsubscribe) {
      this.unsubscribe();
    }
  }
}

export class ValidTaskDescriptionMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const hasDescription = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || hasDescription));
  }
}
