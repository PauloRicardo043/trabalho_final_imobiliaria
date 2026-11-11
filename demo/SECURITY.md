# Autenticação e autorização

Configure `JWT_SECRET` com uma chave Base64 de pelo menos 32 bytes antes de publicar a aplicação. O valor padrão em `application.properties` serve apenas para desenvolvimento local. Tokens expiram em uma hora por padrão; o prazo pode ser alterado com `JWT_EXPIRATION_MS`.

| Ação | Rota | Acesso |
| --- | --- | --- |
| Cadastrar usuário comum | `POST /public/register` | Público |
| Login | `POST /public/login` | Público |
| Listar/consultar imóveis | `GET /imoveis`, `GET /imoveis/{id}` | Público |
| Enviar interesse em imóvel | `POST /imoveis/sabermais/{id}` | Público |
| Criar/editar/excluir imóveis | `POST`, `PUT`, `DELETE /imoveis/**` | `ADMIN` |
| Alterar status de imóvel | `PATCH` ou `PUT /imoveis/{id}/status` | Qualquer usuário autenticado |
| Cadastrar/gerenciar usuários | `/users/**` | `ADMIN` |
| Gerenciar bairros, tipos de imóvel e fotos | `/bairros/**`, `/tipos_imoveis/**`, `/fotos/**` | `ADMIN` |

O cadastro público sempre cria um usuário `USER`, mesmo se a requisição enviar `tipo` ou `role` com outro valor. Para provisionar o primeiro administrador, cadastre uma conta comum e altere seu campo `tipo` para `ADMIN` diretamente no banco. Depois, use `POST /users` para cadastrar administradores e demais usuários. Senhas são armazenadas com BCrypt.

Envie tokens no cabeçalho `Authorization: Bearer <token>`. Requisições sem token ou com token inválido/expirado para rotas protegidas retornam `401`; token válido sem permissão suficiente retorna `403`.
