import {NgModule} from '@angular/core';
import {LabelsComponent} from './labels.component';
import {SpineWebClientModule} from '../spine-web-client/spine-web-client.module';

@NgModule({
  declarations: [LabelsComponent],
  imports: [SpineWebClientModule]
})
export class LabelsModule { }
