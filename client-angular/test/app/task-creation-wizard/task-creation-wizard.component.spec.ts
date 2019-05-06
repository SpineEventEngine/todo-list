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

import {ChangeDetectorRef} from '@angular/core';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {Location} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap} from '@angular/router';

import {Client} from 'spine-web';

import {
  MatChipsModule,
  MatIconModule,
  MatListModule,
  MatProgressBarModule,
  MatProgressSpinnerModule
} from '@angular/material';
import {MatMomentDateModule} from '@angular/material-moment-adapter';
import {MatButtonModule} from '@angular/material/button';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatStepperModule} from '@angular/material/stepper';

import {TaskCreationWizardComponent} from 'app/task-creation-wizard/task-creation-wizard.component';
import {TaskDefinitionComponent} from 'app/task-creation-wizard/step-1-task-definition/task-definition.component';
import {LabelAssignmentComponent} from 'app/task-creation-wizard/step-2-label-assignment/label-assignment.component';
import {ConfirmationComponent} from 'app/task-creation-wizard/step-3-confirmation/confirmation.component';
import {TaskCreationWizardRoutingModule} from 'app/task-creation-wizard/task-creation-wizard.routes';
import {TaskServiceModule} from 'app/task-service/task-service.module';
import {TodoListComponentsModule} from 'app/common-components/todo-list-components.module';
import {TodoListPipesModule} from 'app/pipes/todo-list-pipes.module';
import {LabelsModule} from 'app/labels/labels.module';
import {TaskCreationWizard} from 'app/task-creation-wizard/service/task-creation-wizard.service';
import {TaskService} from 'app/task-service/task.service';
import {mockSpineWebClient, subscriptionDataOf} from 'test/given/mock-spine-web-client';
import {chores} from 'test/given/tasks';
import {LabelService} from 'app/labels/label.service';
import {initMockProcess, taskCreationProcess} from 'test/given/task-creation-process';

import {TaskCreation} from 'proto/todolist/model_pb';
import {mockStepper} from 'test/task-creation-wizard/given/mock-stepper';
import {mockLayoutService, mockNotificationService} from 'test/given/layout-service';
import {LayoutService} from 'app/layout/layout.service';
import {NotificationService} from 'app/layout/notification.service';
import {LayoutModule} from 'app/layout/layout.module';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatCardModule} from '@angular/material/card';

describe('TaskCreationWizardComponent', () => {
  const mockClient = mockSpineWebClient();
  const unsubscribe = jasmine.createSpy('unsubscribe');
  mockClient.subscribeToEntities.and.returnValue(subscriptionDataOf(
    [chores()], [], [], unsubscribe
  ));
  const layoutService = mockLayoutService();
  const notificationService = mockNotificationService();
  const fetch = jasmine.createSpyObj<Client.Fetch>('Fetch', ['atOnce']);
  mockClient.fetchAll.and.returnValue(fetch);
  fetch.atOnce.and.returnValue(Promise.resolve());

  // It's actually not important which ID we have in route as initialization is done via mocks.
  // It's only important for it to be defined.
  const activatedRoute = {
    snapshot: {
      paramMap: convertToParamMap({taskCreationId: taskCreationProcess().getId().getValue()})
    }
  };

  let component: TaskCreationWizardComponent;
  let fixture: ComponentFixture<TaskCreationWizardComponent>;

  function initChildElements(wizardComponent: TaskCreationWizardComponent) {
    wizardComponent.stepper = mockStepper();
    wizardComponent.taskDefinition = jasmine.createSpyObj<TaskDefinitionComponent>(
      'TaskDefinitionComponent', ['initFromWizard']
    );
    wizardComponent.labelAssignment = jasmine.createSpyObj<LabelAssignmentComponent>(
      'LabelAssignmentComponent', ['initFromWizard']
    );
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TaskCreationWizardComponent,
        TaskDefinitionComponent,
        LabelAssignmentComponent,
        ConfirmationComponent
      ],
      imports: [
        FormsModule,
        NoopAnimationsModule,
        RouterTestingModule.withRoutes([]),

        TaskCreationWizardRoutingModule,
        TodoListComponentsModule,
        TodoListPipesModule,
        TaskServiceModule,
        LabelsModule,
        LayoutModule,

        MatProgressSpinnerModule,
        MatSnackBarModule,
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
        MatStepperModule,
        MatCardModule
      ],
      providers: [
        TaskCreationWizard,
        TaskService,
        LabelService,
        NotificationService,
        {provide: Client, useValue: mockClient},
        {provide: ActivatedRoute, useValue: activatedRoute},
        {provide: LayoutService, useValue: layoutService},
        {provide: NotificationService, useValue: notificationService}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    mockClient.fetchById.and.callFake(initMockProcess());
    mockClient.sendCommand.and.callFake((command, resolve) => resolve());
  });

  it('should create', () => {
    fixture = TestBed.createComponent(TaskCreationWizardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should re-navigate to URL with process ID if started from `/wizard` URL', fakeAsync(() => {

    // Create a component by hand to use a different activated route.
    const wizard = new TaskCreationWizard(mockClient, new TaskService(mockClient, undefined));
    const changeDetector =
      jasmine.createSpyObj<ChangeDetectorRef>('ChangeDetector', ['detectChanges']);
    const location =
      jasmine.createSpyObj<Location>('Location', ['isCurrentPathEqualTo', 'go']);
    const activated = new ActivatedRoute();
    activated.snapshot = new ActivatedRouteSnapshot();

    const theComponent =
      new TaskCreationWizardComponent(wizard, changeDetector, location, activated, layoutService);
    initChildElements(theComponent);
    theComponent.ngAfterViewInit();
    tick();
    const wizardId = wizard.id.getValue();
    expect(wizardId).toBeTruthy();
    expect(location.go).toHaveBeenCalledWith(`/wizard/${wizardId}`);
  }));

  it('should throw an Error when wizard initialization fails', fakeAsync(() => {
    mockClient.fetchById.and.callFake((command, resolve, reject) => reject());

    fixture = TestBed.createComponent(TaskCreationWizardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(() => {
      component.ngAfterViewInit();
      tick();
    }).toThrowError();
  }));

  it('should select the step in stepper according to the creation process stage', fakeAsync(() => {
    fixture = TestBed.createComponent(TaskCreationWizardComponent);
    component = fixture.componentInstance;
    initChildElements(component);
    fixture.detectChanges();

    component.ngAfterViewInit();
    tick();
    expect(component.stepper.selectedIndex).toEqual(TaskCreation.Stage.LABEL_ASSIGNMENT);
  }));

  it('should throw an Error when trying to navigate to unknown stage', fakeAsync(() => {
    mockClient.fetchById.and.callFake(initMockProcess(TaskCreation.Stage.TCS_UNKNOWN));

    fixture = TestBed.createComponent(TaskCreationWizardComponent);
    component = fixture.componentInstance;
    initChildElements(component);
    fixture.detectChanges();

    expect(() => {
      component.ngAfterViewInit();
      tick();
    }).toThrowError();
  }));

  it('should change the label on the toolbar', fakeAsync(() => {
    TestBed.createComponent(TaskCreationWizardComponent);
    tick();
    expect(layoutService.update).toHaveBeenCalledWith({toolbarLabel: 'Create a task', showNavigation: false});
  }));

  it('should execute child components initialization', fakeAsync(() => {
    fixture = TestBed.createComponent(TaskCreationWizardComponent);
    component = fixture.componentInstance;
    initChildElements(component);
    fixture.detectChanges();

    component.ngAfterViewInit();
    tick();
    expect(component.taskDefinition.initFromWizard).toHaveBeenCalled();
    expect(component.labelAssignment.initFromWizard).toHaveBeenCalled();
  }));
});
