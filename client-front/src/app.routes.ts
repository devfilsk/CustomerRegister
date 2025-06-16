import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout.component';
import { ClientListComponent } from './components/clients/client-list/client-list.component';
import { ClientFormComponent } from './components/clients/client-form/client-form.component';
import { ClientDetailsComponent } from './components/clients/client-details/client-details.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: 'clients',
        component: ClientListComponent
      },
      {
        path: 'clients/new',
        component: ClientFormComponent
      },
      {
        path: 'clients/:id',
        component: ClientDetailsComponent
      },
      {
        path: 'clients/edit/:id',
        component: ClientFormComponent
      },
      {
        path: '',
        redirectTo: 'clients',
        pathMatch: 'full'
      }
    ]
  }
]; 