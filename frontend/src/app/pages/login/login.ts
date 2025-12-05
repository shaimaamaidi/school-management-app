import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/services-custom/authService/auth-service';

@Component({
  selector: 'app-login',
  standalone:true,
  imports: [FormsModule,CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  
  constructor(
    private authService : AuthService,
    private router : Router
  ){}

  handleNavClick() {
    this.router.navigateByUrl('signup');
  }

  username = '';
  password = '';
  showPassword = false;
  error : string | null =null;
  showSignup = false;


  handleSubmit() {
    this.error = '';

    if (!this.username.trim()) {
      this.error=("Username is required!");
      return;
    }
    if (!this.password) {
      this.error=('Password is required!');
      return;
    }

    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        this.router.navigateByUrl('/admin');
        this.error=null;
      }
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }
}
