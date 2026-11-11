
export function salvarToken(token, tipo, perfil) {
    localStorage.setItem('token', token);
    localStorage.setItem('tipo', tipo);
    localStorage.setItem('perfil', perfil);
}

export function obterToken() {
    return localStorage.getItem('token');
}

export function obterPerfil() {
    return localStorage.getItem('perfil');
}

export function estaLogado() {
    return !!obterToken();
}

export function realizarLogout() {
    localStorage.removeItem('token');
    localStorage.removeItem('tipo');
    localStorage.removeItem('perfil');
}
