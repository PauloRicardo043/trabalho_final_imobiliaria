
<script setup>
import { useRouter } from 'vue-router'
import { realizarLogout } from '../services/auth'

const router = useRouter()

function sair() {
  realizarLogout()
  router.push('/login')
}
</script>

<template>
  <main class="acesso">
    <h1>Login realizado!</h1>

    <p>Seu token de autenticação foi recebido e armazenado.</p>

    <p>
      Estamos preparando o redirecionamento conforme seu perfil de usuário.
    </p>

    <button @click="sair">Sair</button>
  </main>
</template>

<style scoped>
.acesso {
  max-width: 700px;
  margin: 60px auto;
  padding: 30px;
  text-align: center;
  background: white;
  border-radius: 10px;
}

button {
  padding: 12px 25px;
  background: #244b70;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>
