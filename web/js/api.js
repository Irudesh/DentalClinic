

const Api = {
  base: "",

  getToken() {
    return sessionStorageSafeGet("sdc_token");
  },
  getRole() {
    return sessionStorageSafeGet("sdc_role");
  },
  getFullName() {
    return sessionStorageSafeGet("sdc_fullName");
  },
  setSession(token, role, fullName) {
    sessionStorageSafeSet("sdc_token", token);
    sessionStorageSafeSet("sdc_role", role);
    sessionStorageSafeSet("sdc_fullName", fullName || "");
  },
  clearSession() {
    sessionStorageSafeSet("sdc_token", "");
    sessionStorageSafeSet("sdc_role", "");
    sessionStorageSafeSet("sdc_fullName", "");
  },
  requireLogin() {
    if (!this.getToken()) {
      window.location.href = "login.html";
    }
  },
  requireAdmin() {
    this.requireLogin();
    if (this.getRole() !== "ADMIN") {
      window.location.href = "dashboard.html";
    }
  },

  async post(path, formFields) {
    const body = new URLSearchParams(formFields || {});
    const res = await fetch(this.base + path, { method: "POST", body });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.error || ("Request failed (" + res.status + ")"));
    return data;
  },

  async get(path, params) {
    const query = new URLSearchParams(params || {});
    const res = await fetch(this.base + path + "?" + query.toString());
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.error || ("Request failed (" + res.status + ")"));
    return data;
  },

  async postAuthed(path, formFields) {
    return this.post(path, Object.assign({ token: this.getToken() }, formFields || {}));
  },

  async getAuthed(path, params) {
    return this.get(path, Object.assign({ token: this.getToken() }, params || {}));
  }
};

// In-memory fallback in case sessionStorage is unavailable (e.g. some sandboxed previews).
const _memoryStore = {};
function sessionStorageSafeGet(key) {
  try { return sessionStorage.getItem(key) || ""; } catch (e) { return _memoryStore[key] || ""; }
}
function sessionStorageSafeSet(key, value) {
  try { sessionStorage.setItem(key, value); } catch (e) { _memoryStore[key] = value; }
}

function showMessage(el, text, isError) {
  el.textContent = text;
  el.className = "message " + (isError ? "error" : "ok");
}
