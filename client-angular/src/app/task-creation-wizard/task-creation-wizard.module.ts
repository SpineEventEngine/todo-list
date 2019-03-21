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

import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';

import {MatIconModule} from '@angular/material';
import {MatMomentDateModule} from '@angular/material-moment-adapter';
import {MatButtonModule} from '@angular/material/button';
import {MatChipsModule} from '@angular/material/chips';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatListModule} from '@angular/material/list';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatSelectModule} from '@angular/material/select';
import {MatStepperModule} from '@angular/material/stepper';

import {TaskDefinitionComponent} from './step-1-task-definition/task-definition.component';
import {LabelAssignmentComponent} from './step-2-label-assignment/label-assignment.component';
import {TaskCreationWizardComponent} from './task-creation-wizard.component';
import {ConfirmationComponent} from './step-3-confirmation/confirmation.component';
import {TaskCreationWizardRoutingModule} from './task-creation-wizard.routes';
import {TodoListComponentsModule} from '../common-components/todo-list-components.module';
import {SpineClientProvider} from '../spine-client-provider/spine-client-provider.module';
import {TodoListPipesModule} from '../pipes/todo-list-pipes.module';
import {TaskServiceModule} from '../task-service/task-service.module';
import {LabelsModule} from '../labels/labels.module';

/**
 * The module which provides a wizard for step-by-step task creation.
 *
 * @see `io.spine.examples.todolist.c.procman.TaskCreationWizard` in `api-java`
 */
@NgModule({
  declarations: [
    TaskCreationWizardComponent,
    TaskDefinitionComponent,
    LabelAssignmentComponent,
    ConfirmationComponent
  ],
  imports: [
    CommonModule,
    FormsModule,

    TaskCreationWizardRoutingModule,
    TodoListComponentsModule,
    SpineClientProvider,
    TodoListPipesModule,
    TaskServiceModule,
    LabelsModule,

    MatMomentDateModule,
    MatButtonModule,
    MatChipsModule,
    MatDatepickerModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatListModule,
    MatProgressBarModule,
    MatSelectModule,
    MatStepperModule
  ]
})
export class TaskCreationWizardModule {
}
