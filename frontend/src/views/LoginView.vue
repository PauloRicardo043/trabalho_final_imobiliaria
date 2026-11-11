
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { realizarLogin } from '../services/api'
import { salvarToken } from '../services/auth'

const router = useRouter()

const email = ref('')
const senha = ref('')
const carregando = ref(false)
const erro = ref('')

async function entrar() {
  erro.value = ''

  if (!email.value || !senha.value) {
    erro.value = 'Preencha todos os campos.'
    return
  }

  const formatoEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+>$/

  if (!formatoEmail.test(email.value)) {
    erro.value = 'Digite um endereço de e-mail válido.'
    return
  }

  carregando.value = true

  try {
    const resposta = await realizarLogin(email.value, senha.value)

    if (!resposta?.token) {
      erro.value = 'O servidor não retornou um token válido.'
      return
    }

    salvarToken(resposta.token, resposta.tipo, resposta.perfil);

    // O perfil ainda precisa ser confirmado com o backend.
    if (resposta.perfil === 'ROLE_ADMIN') {
  router.push('/admin')
} else {
  router.push('/cliente')
}

  } catch (erroApi) {
    if (erroApi.status === 401) {
      erro.value = 'E-mail ou senha incorretos.'
    } else {
      erro.value = erroApi.message || 'Não foi possível realizar o login.'
    }
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <main class="pagina-login">
    <form class="formulario" @submit.prevent="entrar">
      <h1>Entrar</h1>

      <p>Acesse sua conta para continuar.</p>

      <label for="email">E-mail</label>
      <input
        id="email"
        v-model.trim="email"
        type="email"
        placeholder="seuemail@email.com"
        required
      />

      <label for="senha">Senha</label>
      <input
        id="senha"
        v-model="senha"
        type="password"
        placeholder="Digite sua senha"
        required
      />

      <p v-if="erro" class="erro">{{ erro }}</p>

      <button type="submit" :disabled="carregando">
        {{ carregando ? 'Entrando...' : 'Entrar' }}
      </button>

      <RouterLink to="/">Voltar para a vitrine</RouterLink>
    </form>
  </main>
</template>

<style scoped>

.pagina-login {
  min-height: 80vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  background: #f5f7fa;
}

.formulario {
  width: 100%;
  max-width: 440px;
  padding: 38px;
  background: white;
  border: 1px solid #e5e9ee;
  border-radius: 14px;
  box-shadow: 0 6px 24px #173b5e15;
  display: flex;
  flex-direction: column;
  gap: 13px;
}

.formulario h1 {
  color: #173b5e;
  font-size: 30px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 0;
}

.formulario > p {
  color: #687887;
  text-align: center;
  margin-bottom: 15px;
}

.formulario label {
  color: #244b70;
  font-size: 14px;
  font-weight: bold;
  margin-top: 5px;
}

.formulario input {
  width: 100%;
  padding: 14px;
  border: 1px solid #d5dce4;
  border-radius: 7px;
  background: #ffffff;
  color: #333333;
  font-size: 15px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.formulario input:focus {
  outline: none;
  border-color: #356b9a;
  box-shadow: 0 0 0 3px #356b9a20;
}

.formulario button {
  width: 100%;
  padding: 14px;
  margin-top: 12px;
  background: #173b5e;
  color: white;
  border: none;
  border-radius: 7px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}

.formulario button:hover {
  background: #285b87;
}

.formulario button:disabled {
  background: #8295a7;
  cursor: not-allowed;
}

.formulario a {
  margin-top: 8px;
  text-align: center;
  color: #285b87;
  font-weight: bold;
  font-size: 14px;
}

.formulario a:hover {
  color: #173b5e;
  text-decoration: underline;
}

.erro {
  color: #b42318 !important;
  background: #fff0ef;
  border: 1px solid #f5c2be;
  border-radius: 6px;
  padding: 10px;
  font-size: 14px;
  text-align: left !important;
}

@media (max-width: 500px) {
  .formulario {
    padding: 25px 20px;
  }

  .formulario h1 {
    font-size: 26px;
  }
}

</style>
