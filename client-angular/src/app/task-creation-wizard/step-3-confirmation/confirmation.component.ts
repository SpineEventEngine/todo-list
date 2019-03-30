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

import {Component} from '@angular/core';
import {Router} from '@angular/router';

import {TaskCreationWizard} from 'app/task-creation-wizard/service/task-creation-wizard.service';
import {WizardStep} from 'app/task-creation-wizard/wizard-step';

/**
 * A component which represents the third step of the Task Creation Wizard - the confirmation.
 *
 * This is the final step of a wizard and it allows user to either complete or cancel the task
 * creation (or to navigate back and further modify data).
 *
 * Depending on the chosen action, the corresponding command will be sent to the server and the
 * client will navigate away from the wizard.
 *
 * In case of successful task creation, it should appear on `/task-list/active` page in the list of
 * active tasks.
 */
@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html'
})
export class ConfirmationComponent extends WizardStep {

  constructor(router: Router, wizard: TaskCreationWizard) {
    super(router, wizard);
  }

  /**
   * @inheritDoc
   */
  protected doStep(): Promise<void> {
    return this.wizard.completeTaskCreation();
  }
}