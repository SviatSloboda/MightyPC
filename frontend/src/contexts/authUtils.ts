export function googleLogin() {
    const host = window.location.host === "localhost:5173" ? "http://localhost:8080" : window.location.origin;

    window.location.href = host + "/oauth2/authorization/google";
}