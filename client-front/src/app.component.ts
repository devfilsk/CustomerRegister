import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { PoModule } from '@po-ui/ng-components';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, PoModule, HttpClientModule],
  template: `<router-outlet></router-outlet>`,
  styles: []
})
export class AppComponent {
  title = 'client-front';
} 