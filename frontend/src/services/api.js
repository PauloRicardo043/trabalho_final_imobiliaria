
import { obterToken, realizarLogout } from './auth'

const API_URL = ''

export async function requisicao(url, opcoes = {}) {
  const token = obterToken()

  const headers = {
    'Content-Type': 'application/json',
    ...opcoes.headers
  }

  // Se existir token, envia na requisição
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const resposta = await fetch(`${API_URL}${url}`, {
    ...opcoes,
    headers
  })

  const dados = await resposta.json().catch(() => null)

  // Se o token for inválido ou estiver expirado
  if (resposta.status === 401 && token) {
    realizarLogout()

    if (window.location.pathname !== '/login') {
      window.location.href = '/login'
    }

    throw new Error('Sua sessão expirou. Faça login novamente.')
  }

  if (!resposta.ok) {
    const erro = new Error(
      dados?.message || dados?.error || 'Erro ao comunicar com o servidor.'
    )

    erro.status = resposta.status
    throw erro
  }

  return dados
}

// Lista imóveis
export function listarImoveis() {
  return requisicao('/imoveis')
}

// Busca imóvel pelo ID
export function buscarImovel(id) {
  return requisicao(`/imoveis/${id}`)
}

// Realiza login
export function realizarLogin(email, senha) {
  return requisicao('/public/login', {
    method: 'POST',
    body: JSON.stringify({ email, senha })
  })
}
