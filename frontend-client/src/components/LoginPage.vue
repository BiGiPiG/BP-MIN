<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi, ApiError } from '@/api'

const router = useRouter()

// Form fields
const email = ref('')
const password = ref('')
const username = ref('')
const nickname = ref('')
const confirmPassword = ref('')
const showLogin = ref(true)

// Errors
const usernameError = ref('')
const emailError = ref('')
const passwordError = ref('')

const particles = ref([])

onMounted(() => {
  const arr = []
  for (let i = 0; i < 25; i++) {
    arr.push({
      id: i,
      top: Math.random() * 100,
      left: Math.random() * 100,
      size: 8 + (i % 12),
      delay: (i % 5) * 0.8,
      duration: 3 + (i % 4)
    })
  }
  particles.value = arr
})

const toggleForm = () => {
  showLogin.value = !showLogin.value
  usernameError.value = ''
  emailError.value = ''
  passwordError.value = ''
}

const parseJwt = (token) => {
  try {
    return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')))
  } catch (e) {
    return null
  }
}

const handleSignin = async () => {
  usernameError.value = ''
  passwordError.value = ''

  if (!username.value.trim() || !password.value.trim()) {
    passwordError.value = 'Please fill in all fields'
    return
  }

  try {
    const data = await authApi.signin({
      username: username.value,
      password: password.value
    })

    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)

    const payload = parseJwt(data.accessToken)
    const storedUsername = payload?.sub
    const userId = payload?.userId

    if (storedUsername) {
      localStorage.setItem('username', storedUsername)
      localStorage.setItem('userId', userId)
      await router.push(`/bp-min/${storedUsername}`)
    }
  } catch (error) {
    if (error instanceof ApiError) {
      passwordError.value =
        error.fieldError('password') ||
        error.fieldError('username') ||
        (error.code === 'AUTH_FAILED' || error.code === 'USER_NOT_FOUND'
          ? 'Invalid username or password'
          : error.message)
      return
    }

    console.error('Network error:', error)
    passwordError.value = 'Connection failed. Please try again.'
  }
}

const handleSignup = async () => {
  usernameError.value = ''
  emailError.value = ''
  passwordError.value = ''

  // Validations
  if (password.value !== confirmPassword.value) {
    passwordError.value = 'Passwords do not match'
    return
  }

  if (password.value.length < 6) {
    passwordError.value = 'Password must be at least 6 characters'
    return
  }

  try {
    await authApi.signup({
      nickname: nickname.value,
      username: username.value,
      email: email.value,
      password: password.value
    })

    toggleForm()
  } catch (error) {
    if (error instanceof ApiError) {
      // Коды из ErrorResponse, см. api/openapi.yaml
      if (error.code === 'USERNAME_INVALID') {
        usernameError.value = 'Username is already taken'
      } else if (error.code === 'EMAIL_INVALID') {
        emailError.value = 'Email is already taken'
      } else if (error.code === 'VALIDATION_FAILED') {
        usernameError.value = error.fieldError('username') || ''
        emailError.value = error.fieldError('email') || ''
        passwordError.value =
          error.fieldError('password') || error.fieldError('nickname') || ''
      } else {
        passwordError.value = error.message || 'Registration failed'
      }
      return
    }

    console.error('Network error:', error)
    passwordError.value = 'Connection failed. Please try again.'
  }
}
</script>

<template>
  <div class="login-container">
    <div class="background">
      <div
          v-for="p in particles"
          :key="p.id"
          class="particle"
          :style="{
          top: `${p.top}%`,
          left: `${p.left}%`,
          width: `${p.size}px`,
          height: `${p.size}px`,
          animationDelay: `${p.delay}s`,
          animationDuration: `${p.duration}s`
        }"
      ></div>
    </div>

    <div class="form-container">
      <!-- Sign in form -->
      <div class="form-card login-card" :class="{ active: showLogin }">
        <h2>SIGN IN</h2>
        <div class="input-group">
          <span class="icon">👤</span>
          <input type="text" v-model="username" placeholder="Username" required />
        </div>
        <div v-if="usernameError && showLogin" class="error-message">
          {{ usernameError }}
        </div>

        <div class="input-group">
          <span class="icon">🔒</span>
          <input type="password" v-model="password" placeholder="Password" required />
        </div>
        <div v-if="passwordError && showLogin" class="error-message">
          {{ passwordError }}
        </div>

        <button @click="handleSignin" class="btn sign-in-btn">Sign In</button>
        <p class="switch-form">
          Create Account? <span @click="toggleForm" class="link">Sign Up</span>
        </p>
      </div>

      <!-- Sign up form -->
      <div class="form-card signup-card" :class="{ active: !showLogin }">
        <h2>SIGN UP</h2>

        <div class="input-group">
          <span class="icon">👤</span>
          <input type="text" v-model="nickname" placeholder="Nickname" required />
        </div>

        <div class="input-group">
          <span class="icon">👤</span>
          <input type="text" v-model="username" placeholder="Username" required />
        </div>
        <div v-if="usernameError && !showLogin" class="error-message">{{ usernameError }}</div>

        <div class="input-group">
          <span class="icon">✉️</span>
          <input type="email" v-model="email" placeholder="Email" required />
        </div>
        <div v-if="emailError" class="error-message">{{ emailError }}</div>

        <div class="input-group">
          <span class="icon">🔑</span>
          <input type="password" v-model="password" placeholder="Password" required />
        </div>

        <div class="input-group">
          <span class="icon">🔄</span>
          <input type="password" v-model="confirmPassword" placeholder="Confirm Password" required />
        </div>
        <div v-if="passwordError && !showLogin" class="error-message">
          {{ passwordError }}
        </div>

        <button @click="handleSignup" class="btn signup-btn">Sign Up</button>
        <p class="switch-form">
          Already have account? <span @click="toggleForm" class="link">Sign In</span>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ────────────────────────────────────
   General settings
   ──────────────────────────────────── */
*, *::before, *::after {
  box-sizing: border-box;
}

/* ────────────────────────────────────
   Controllers and background
   ──────────────────────────────────── */
.login-container {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
  font-family: 'Segoe UI', system-ui, sans-serif;
  background: linear-gradient(135deg, #8e44ad, #6a3093);
}

.background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  overflow: hidden;
}

/* ────────────────────────────────────
   Particulars
   ──────────────────────────────────── */
.particle {
  position: absolute;
  z-index: 2;
  width: 12px;
  height: 12px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
  animation: float 5s infinite ease-in-out;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.7;
  }
  25% {
    transform: translate(10px, -15px) scale(1.1);
    opacity: 1;
  }
  50% {
    transform: translate(-5px, 10px) scale(0.9);
    opacity: 0.8;
  }
  75% {
    transform: translate(15px, 5px) scale(1.05);
    opacity: 0.9;
  }
}

/* ────────────────────────────────────
   Forms and cards
   ──────────────────────────────────── */
.form-container {
  display: flex;
  gap: 20px;
  position: relative;
  z-index: 3;
}

.form-card {
  width: 300px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.4s ease;
}

.form-card:not(.active) {
  position: absolute;
  opacity: 0;
  transform: translateY(20px);
  pointer-events: none;
}

.form-card.active {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}

/* ────────────────────────────────────
   Headers
   ──────────────────────────────────── */
.form-card h2 {
  margin: 0 0 20px;
  font-size: 24px;
  font-weight: bold;
  color: #333;
  text-align: center;
  font-family: "Raleway ExtraBold", serif;
}

/* ────────────────────────────────────
   input fields
   ──────────────────────────────────── */
.input-group {
  position: relative;
  width: 100%;
  margin-bottom: 20px;
}

.input-group .icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  color: #888;
  font-size: 16px;
  pointer-events: none;
}

.input-group input {
  width: 100%;
  padding: 12px 15px 12px 48px;
  border: 1px solid #ddd;
  border-radius: 15px;
  background: #fafafa;
  color: #333;
  font: inherit;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s, box-shadow 0.3s;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.input-group input::placeholder {
  color: #aaa;
}

.input-group input:focus {
  border-color: #8e44ad;
  box-shadow: 0 0 0 3px rgba(138, 43, 226, 0.15);
}

/* ────────────────────────────────────
   Error message
   ──────────────────────────────────── */
.error-message {
  width: 100%;
  margin-top: -12px;
  margin-bottom: 12px;
  font-size: 12px;
  color: #e74c3c;
  text-align: left;
}

/* ────────────────────────────────────
   Buttons
   ──────────────────────────────────── */
.btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 15px;
  font-size: 16px;
  font-weight: bold;
  color: white;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.sign-in-btn {
  background: linear-gradient(45deg, #ff6b6b, #ff8e53);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.sign-in-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.signup-btn {
  background: linear-gradient(45deg, #9b59b6, #8e44ad);
  box-shadow: 0 4px 15px rgba(155, 89, 182, 0.3);
}

.signup-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(155, 89, 182, 0.4);
}

/* ────────────────────────────────────
   Forms switching
   ──────────────────────────────────── */
.switch-form {
  margin-top: 20px;
  font-size: 14px;
  color: #666;
  text-align: center;
}

.switch-form .link {
  color: #8e44ad;
  text-decoration: underline;
  font-weight: bold;
  cursor: pointer;
}

/* ────────────────────────────────────
   Adaptability
   ──────────────────────────────────── */
@media (max-width: 768px) {
  .form-container {
    flex-direction: column;
    align-items: center;
  }

  .form-card {
    width: 90%;
    max-width: 350px;
  }
}

@media (max-width: 480px) {
  .form-card {
    padding: 20px;
  }

  .form-card h2 {
    font-size: 20px;
  }

  .input-group input {
    padding: 10px 12px 10px 40px;
    font-size: 13px;
  }

  .btn {
    padding: 10px;
    font-size: 14px;
  }
}
</style>