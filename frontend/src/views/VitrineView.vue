
<script setup>
import { ref, computed, onMounted } from 'vue'
import { listarImoveis } from '../services/api'

const imoveis = ref([])
const carregando = ref(true)
const erro = ref('')

const busca = ref('')
const finalidade = ref('')
const ordenacao = ref('menor')
const pagina = ref(1)

const porPagina = 6

async function carregarImoveis() {
  carregando.value = true
  erro.value = ''

  try {
    const dados = await listarImoveis()

    // Evita exibir registros sem título
    imoveis.value = dados.filter(imovel =>
      imovel.titulo && imovel.titulo.trim() !== ''
    )
  } catch (erroApi) {
    console.error(erroApi)
    erro.value = erroApi.status === 401
      ? 'A API exige autenticação para consultar os imóveis.'
      : 'Não foi possível carregar os imóveis. Verifique o backend.'
  } finally {
    carregando.value = false
  }
}

function precoImovel(imovel) {
  if (imovel.precoVenda != null) {
    return Number(imovel.precoVenda)
  }

  if (imovel.precoAluguel != null) {
    return Number(imovel.precoAluguel)
  }

  return 0
}

function formatarPreco(valor) {
  if (valor == null) return 'Consulte o valor'

  return Number(valor).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  })
}

const imoveisFiltrados = computed(() => {
  let resultado = imoveis.value.filter(imovel => {
    const texto = busca.value.toLowerCase()

    const conteudo = [
      imovel.titulo,
      imovel.descricao,
      imovel.endereco,
      imovel.bairro?.nome,
      imovel.bairro?.cidade
    ].filter(Boolean).join(' ').toLowerCase()

    const correspondeBusca = conteudo.includes(texto)

    const correspondeFinalidade =
      !finalidade.value ||
      imovel.finalidade === finalidade.value

    return correspondeBusca && correspondeFinalidade
  })

  if (ordenacao.value === 'menor') {
    resultado.sort((a, b) => precoImovel(a) - precoImovel(b))
  } else if (ordenacao.value === 'maior') {
    resultado.sort((a, b) => precoImovel(b) - precoImovel(a))
  } else if (ordenacao.value === 'titulo') {
    resultado.sort((a, b) => a.titulo.localeCompare(b.titulo))
  }

  return resultado
})

const totalPaginas = computed(() =>
  Math.max(1, Math.ceil(imoveisFiltrados.value.length / porPagina))
)

const imoveisPagina = computed(() => {
  const inicio = (pagina.value - 1) * porPagina

  return imoveisFiltrados.value.slice(
    inicio,
    inicio + porPagina
  )
})

function alterarPagina(novaPagina) {
  pagina.value = novaPagina
}

onMounted(carregarImoveis)
</script>

<template>
  <main>
    <section class="apresentacao">
      <div class="largura">
        <h1>Encontre seu próximo imóvel</h1>
        <p>Confira as oportunidades disponíveis para você.</p>
      </div>
    </section>

    <section id="imoveis" class="largura conteudo">
      <h2>Buscar imóveis</h2>

      <div class="filtros">
        <input
          v-model="busca"
          @input="pagina = 1"
          placeholder="Buscar por título, endereço ou cidade"
        />

        <select v-model="finalidade" @change="pagina = 1">
          <option value="">Todas as finalidades</option>
          <option
            v-for="item in [...new Set(imoveis.map(i => i.finalidade).filter(Boolean))]"
            :key="item"
            :value="item"
          >
            {{ item }}
          </option>
        </select>

        <select v-model="ordenacao" @change="pagina = 1">
          <option value="menor">Menor preço</option>
          <option value="maior">Maior preço</option>
          <option value="titulo">Nome do imóvel</option>
        </select>
      </div>

      <p v-if="carregando">Carregando imóveis...</p>

      <div v-else-if="erro" class="mensagem erro">
        {{ erro }}
        <button @click="carregarImoveis">Tentar novamente</button>
      </div>

      <p v-else-if="imoveisFiltrados.length === 0">
        Nenhum imóvel encontrado.
      </p>

      <template v-else>
        <p class="quantidade">
          {{ imoveisFiltrados.length }} imóvel(is) encontrado(s)
        </p>

        <div class="lista">
          <article
            v-for="imovel in imoveisPagina"
            :key="imovel.id"
            class="card"
          >
            <div class="imagem-imovel">
              {{ imovel.tipoImovel?.nome || 'Imóvel' }}
            </div>

            <div class="card-conteudo">
              <span v-if="imovel.destaque" class="destaque">
                Destaque
              </span>

              <h3>{{ imovel.titulo }}</h3>

              <p class="local">
                {{ imovel.bairro?.nome || 'Bairro não informado' }}
                <span v-if="imovel.bairro?.cidade">
                  - {{ imovel.bairro.cidade }}/{{ imovel.bairro.estado }}
                </span>
              </p>

              <p class="preco">
                {{ formatarPreco(imovel.precoVenda ?? imovel.precoAluguel) }}
              </p>

              <div class="caracteristicas">
                <span>{{ imovel.dormitorios ?? 0 }} dormitórios</span>
                <span>{{ imovel.banheiros ?? 0 }} banheiros</span>
                <span>{{ imovel.garagem ?? 0 }} vagas</span>
                <span>{{ imovel.areaTotal ?? 0 }} m²</span>
              </div>

              <RouterLink
                :to="`/imovel/${imovel.id}`"
                class="botao"
              >
                Ver detalhes
              </RouterLink>
            </div>
          </article>
        </div>

        <div class="paginacao" v-if="totalPaginas > 1">
          <button
            :disabled="pagina === 1"
            @click="alterarPagina(pagina - 1)"
          >
            Anterior
          </button>

          <span>Página {{ pagina }} de {{ totalPaginas }}</span>

          <button
            :disabled="pagina === totalPaginas"
            @click="alterarPagina(pagina + 1)"
          >
            Próxima
          </button>
        </div>
      </template>
    </section>
  </main>
</template>

<style scoped>

.largura {
  width: 100%;
  max-width: 1200px;
  margin: auto;
  padding: 0 25px;
}

/* Apresentação */
.apresentacao {
  background: linear-gradient(135deg, #e4edf5, #f5f8fb);
  padding: 65px 0;
  border-bottom: 1px solid #e0e6ec;
}

.apresentacao h1 {
  color: #173b5e;
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 10px;
}

.apresentacao p {
  color: #586b7d;
  font-size: 17px;
}

/* Conteúdo */
.conteudo {
  padding-top: 40px;
  padding-bottom: 50px;
}

.conteudo h2 {
  color: #173b5e;
  font-size: 25px;
  font-weight: bold;
  margin-bottom: 20px;
}

/* Filtros */
.filtros {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 15px;
  margin-bottom: 25px;
}

.filtros input,
.filtros select {
  width: 100%;
  padding: 14px;
  border: 1px solid #d5dce4;
  border-radius: 8px;
  background: white;
  color: #333;
  font-size: 14px;
}

.filtros input:focus,
.filtros select:focus {
  outline: none;
  border-color: #356b9a;
  box-shadow: 0 0 0 3px #356b9a20;
}

/* Quantidade de imóveis */
.quantidade {
  color: #687887;
  font-size: 14px;
  margin-bottom: 18px;
}

/* Lista de imóveis */
.lista {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
}

/* Card */
.card {
  background: white;
  border: 1px solid #e5e9ee;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 14px #173b5e12;
  transition: transform 0.2s, box-shadow 0.2s;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 22px #173b5e20;
}

/* Espaço da imagem */
.imagem-imovel {
  height: 190px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dce7f0, #c7d7e5);
  color: #244b70;
  font-size: 23px;
  font-weight: bold;
}

/* Conteúdo do card */
.card-conteudo {
  padding: 22px;
}

.card h3 {
  color: #173b5e;
  font-size: 20px;
  font-weight: bold;
  margin: 12px 0 8px;
}

.destaque {
  display: inline-block;
  background: #e1f4e8;
  color: #16834a;
  padding: 5px 11px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.local {
  color: #718096;
  font-size: 14px;
  margin-bottom: 12px;
}

.preco {
  color: #16834a;
  font-size: 23px;
  font-weight: bold;
  margin: 12px 0;
}

/* Características */
.caracteristicas {
  display: flex;
  flex-wrap: wrap;
  gap: 9px;
  margin: 18px 0;
}

.caracteristicas span {
  background: #f1f4f7;
  color: #536477;
  padding: 6px 9px;
  border-radius: 5px;
  font-size: 12px;
}

/* Botão detalhes */
.botao {
  display: block;
  width: 100%;
  background: #173b5e;
  color: white;
  text-align: center;
  padding: 12px;
  border-radius: 7px;
  font-weight: bold;
  transition: background 0.2s;
}

.botao:hover {
  background: #285b87;
}

/* Paginação */
.paginacao {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 18px;
  margin: 35px 0;
}

.paginacao button,
.mensagem button {
  padding: 11px 18px;
  border: none;
  border-radius: 6px;
  background: #173b5e;
  color: white;
  cursor: pointer;
}

.paginacao button:disabled {
  background: #aab5bf;
  cursor: not-allowed;
}

.paginacao span {
  color: #536477;
}

/* Mensagens */
.erro {
  color: #b42318;
  padding: 15px 0;
}

/* Celulares */
@media (max-width: 700px) {
  .largura {
    padding: 0 16px;
  }

  .apresentacao {
    padding: 40px 0;
  }

  .apresentacao h1 {
    font-size: 27px;
  }

  .filtros {
    grid-template-columns: 1fr;
  }

  .lista {
    grid-template-columns: 1fr;
  }

  .paginacao {
    gap: 10px;
    flex-wrap: wrap;
  }
}

</style>
