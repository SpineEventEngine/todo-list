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

import {AfterViewInit, Input, ViewChild} from '@angular/core';
import {MatStepper} from '@angular/material';
import {Router} from '@angular/router';

import {TaskCreationWizard} from './service/task-creation-wizard.service';
import {ErrorViewport} from '../common-components/error-viewport/error-viewport.component';

export abstract class WizardStep implements AfterViewInit {

  /** Visible for testing. */
  @Input()
  stepper: MatStepper;

  @ViewChild(ErrorViewport)
  private readonly errorViewport: ErrorViewport;

  protected constructor(private readonly router: Router,
                        protected readonly wizard: TaskCreationWizard) {
  }

  ngAfterViewInit(): void {
    this.initOwnModel();
  }

  /**
   * Inits the own model, not related to the wizard data (like the list of available labels).
   */
  protected initOwnModel(): void {
    // NO-OP by default.
  }

  /**
   * Inits the model entries which represent the data from the wizard (like task definition,
   * priority, etc.).
   *
   * Must be called from the outside when we are sure the wizard data has been fetched.
   */
  initFromWizard() {
    // NO-OP by default.
  }

  /*
  * Navigation methods which can be used by the child components as necessary.
  */

  protected previous(): void {
    this.stepper.previous();
  }

  protected next(): void {
    this.clearError();
    this.doStep()
      .then(() => {
        this.setCompleted();
        this.stepper.next();
      })
      .catch(err => {
        this.reportError(err);
      });
  }

  /**
   * Cancel draft creation.
   */
  protected cancel(): void {
    this.wizard.cancelTaskCreation().then(() => this.goToActiveTasks());
  }

  protected finish() {
    this.doStep().then(() => this.goToActiveTasks());
  }

  protected abstract doStep(): Promise<void>;

  protected onInputChange(): void {
    this.setNotCompleted();
  }

  private setCompleted(): void {
    this.stepper.selected.completed = true;
  }

  private setNotCompleted(): void {
    this.stepper.selected.completed = false;
  }

  private goToActiveTasks(): void {
    // noinspection JSIgnoredPromiseFromCall No navigation result handling necessary.
    this.router.navigate(['/task-list/active']);
  }

  private reportError(err): void {
    this.errorViewport.text = err;
  }

  private clearError(): void {
    this.errorViewport.text = '';
  }
}