import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="layout-container">
      <aside class="sidebar">
        <nav>
          <ul>
            <li>
              <a routerLink="/clients" routerLinkActive="active">Lista de Clientes</a>
            </li>
            <li>
              <a routerLink="/clients/new" routerLinkActive="active">Novo Cliente</a>
            </li>
          </ul>
        </nav>
      </aside>
      <main class="content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .layout-container {
      display: flex;
      min-height: 100vh;
    }

    .sidebar {
      width: 250px;
      background-color: #2c3e50;
      color: white;
      padding: 1rem;
    }

    .sidebar nav ul {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    .sidebar nav ul li {
      margin-bottom: 1rem;
    }

    .sidebar nav ul li a {
      color: white;
      text-decoration: none;
      display: block;
      padding: 0.5rem;
      border-radius: 4px;
      transition: background-color 0.3s;
    }

    .sidebar nav ul li a:hover,
    .sidebar nav ul li a.active {
      background-color: #34495e;
    }

    .content {
      flex: 1;
      padding: 2rem;
      background-color: #f5f6fa;
    }
  `]
})
export class LayoutComponent {} 