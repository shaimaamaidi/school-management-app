import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../services/services-custom/authService/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone:true,
  imports: [CommonModule,FormsModule],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  constructor(
    private authService : AuthService,
    private toastr : ToastrService,
    private router : Router
  ){}

  formData = {
    username: '',
    password: '',
    confirmPassword: ''
  };

  showPassword = false;
  showConfirmPassword = false;
  error : string | null = null;

  handleChange(field: keyof typeof this.formData, value: any) {
    this.formData[field] = value;
    this.error = '';
  }
  handleNavClick(){
    this.router.navigateByUrl('admin');
  }
  handleSubmit() {
    if (!this.formData.username.trim()) { this.error=('Username is required!'); return; }
    if (!this.formData.password) { this.error=('Password is required!'); return; }
    if (this.formData.password.length < 6) { this.error=('The password must contain at least 6 characters!'); return; }
    if (this.formData.password !== this.formData.confirmPassword) { this.error=('Passwords do not match!'); return; }

    this.authService.signup(this.formData.username,this.formData.password).subscribe({
      next: () => {
        this.toastr.success('Account created successfully.');
        this.error=null;
        this.formData = {
          username: '',
          password: '',
          confirmPassword: ''
        };
        setTimeout(() => this.router.navigateByUrl('admin'), 2000);
      }
    });
  }
}
