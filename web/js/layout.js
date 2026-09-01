

const ClinicIcons = {
  home: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 11l9-8 9 8"/><path d="M5 10v10h14V10"/></svg>',
  plus: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>',
  search: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>',
  bill: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2h9l3 3v17H6z"/><path d="M9 8h6M9 12h6M9 16h4"/></svg>',
  help: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="9"/><path d="M9.5 9a2.5 2.5 0 0 1 5 0c0 1.7-2.5 2-2.5 3.5"/><circle cx="12" cy="17" r="0.6" fill="currentColor" stroke="none"/></svg>',
  staff: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="8" r="3.2"/><path d="M2.5 19c1-3.3 3.5-5 6.5-5s5.5 1.7 6.5 5"/><circle cx="18" cy="7" r="2.4"/><path d="M15.8 12.3c2 .4 3.6 1.8 4.3 4.2"/></svg>',
  tooth: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3c-2.6 0-4.7 1.5-4.9 4-.2 2.6.6 3.9.9 6.4.2 2 .1 4.9 1.6 6.5.7.7 1.3-.4 1.5-1.6.2-1.2.2-2.6.9-2.6s.7 1.4.9 2.6c.2 1.2.8 2.3 1.5 1.6 1.5-1.6 1.4-4.5 1.6-6.5.3-2.5 1.1-3.8.9-6.4C16.7 4.5 14.6 3 12 3z"/></svg>',
  reports: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19V9M11 19V4M18 19v-7"/><path d="M2 19h20"/></svg>',
  logout: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="M16 17l5-5-5-5"/><path d="M21 12H9"/></svg>',
  menu: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M3 12h18M3 18h18"/></svg>'
};

function toothMark(size) {
  return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="24" height="24" rx="6" fill="#E8A33D"/>
    <path d="M12 5.2c-2.3 0-4.1 1.3-4.3 3.5-.2 2.3.5 3.4.8 5.6.2 1.7.1 4.3 1.4 5.7.6.6 1.1-.4 1.3-1.4.2-1 .2-2.3.8-2.3s.6 1.3.8 2.3c.2 1 .7 2 1.3 1.4 1.3-1.4 1.2-4 1.4-5.7.3-2.2 1-3.3.8-5.6C16.1 6.5 14.3 5.2 12 5.2z" fill="#084F41"/>
  </svg>`;
}

const NAV_ITEMS = {
  common: [
    { href: "dashboard.html", label: "Home", icon: "home" },
    { href: "register-appointment.html", label: "Register Appointment", icon: "plus" },
    { href: "search-appointment.html", label: "Search Appointment", icon: "search" },
    { href: "billing.html", label: "Calculate & Print Bill", icon: "bill" },
    { href: "help.html", label: "Help", icon: "help" }
  ],
  admin: [
    { href: "admin-staff.html", label: "Staff Accounts", icon: "staff" },
    { href: "admin-dentists.html", label: "Dentists", icon: "tooth" },
    { href: "admin-treatments.html", label: "Treatment Types", icon: "bill" },
    { href: "admin-reports.html", label: "Reports", icon: "reports" }
  ]
};

function currentPage() {
  const path = window.location.pathname.split("/").pop();
  return path || "dashboard.html";
}

function navLinkHtml(item, current) {
  const active = item.href === current ? " active" : "";
  return `<a class="nav-link${active}" href="${item.href}">${ClinicIcons[item.icon]}<span>${item.label}</span></a>`;
}

function buildLayout() {
  const role = Api.getRole();
  const fullName = Api.getFullName() || "Staff";
  const current = currentPage();

  const commonLinks = NAV_ITEMS.common.map(i => navLinkHtml(i, current)).join("");
  const adminLinks = NAV_ITEMS.admin.map(i => navLinkHtml(i, current)).join("");

  const adminGroup = role === "ADMIN" ? `
    <div class="nav-group">
      <div class="nav-eyebrow">Administration</div>
      ${adminLinks}
    </div>` : "";

  const sidebarHtml = `
    <nav class="sidebar" aria-label="Main navigation">
      <div class="brand">
        <span class="brand-mark">${toothMark(30)}</span>
        <span class="brand-text">Sunrise Dental<small>Clinic Operations</small></span>
      </div>
      <div class="nav-group">
        <div class="nav-eyebrow">Front Desk</div>
        ${commonLinks}
      </div>
      ${adminGroup}
      <div class="sidebar-footer">
        <div class="who"><strong>${escapeHtml(fullName)}</strong>
          <span class="role-pill">${role}</span>
        </div>
        <button class="logout-btn" id="sidebarLogout">${ClinicIcons.logout} Exit / Logout</button>
      </div>
    </nav>`;

  const sidebarRoot = document.getElementById("sidebar-root");
  if (sidebarRoot) sidebarRoot.innerHTML = sidebarHtml;

  // Mobile topbar (shown only under the CSS breakpoint)
  const mobileHtml = `
    <div class="mobile-topbar">
      <span class="brand-text">${toothMark(22)} Sunrise Dental</span>
      <button id="mobileMenuBtn" aria-label="Open menu" aria-expanded="false">${ClinicIcons.menu}</button>
    </div>
    <div class="mobile-nav" id="mobileNav">
      ${commonLinks}
      ${role === "ADMIN" ? adminLinks : ""}
      <button class="logout-btn" id="mobileLogout" style="margin-top:10px;">${ClinicIcons.logout} Exit / Logout</button>
    </div>`;
  const mobileRoot = document.getElementById("mobile-nav-root");
  if (mobileRoot) mobileRoot.innerHTML = mobileHtml;

  document.getElementById("sidebarLogout")?.addEventListener("click", doLogout);
  document.getElementById("mobileLogout")?.addEventListener("click", doLogout);
  document.getElementById("mobileMenuBtn")?.addEventListener("click", (e) => {
    const nav = document.getElementById("mobileNav");
    const open = nav.classList.toggle("open");
    e.currentTarget.setAttribute("aria-expanded", String(open));
  });
}

async function doLogout() {
  try { await Api.postAuthed("/api/logout", {}); } catch (e) {}
  Api.clearSession();
  window.location.href = "login.html";
}

function escapeHtml(s) {
  const div = document.createElement("div");
  div.textContent = s;
  return div.innerHTML;
}

document.addEventListener("DOMContentLoaded", buildLayout);
