// Menu ponsel, pratinjau foto, dan konfirmasi aksi.
(() => {
  const nav = document.getElementById('nav');
  const toggle = nav?.querySelector('.nav__toggle');
  const menu = nav?.querySelector('.menu');
  const close = nav?.querySelector('.menu__close');
  if (toggle && menu && close) {
    const open = () => { menu.hidden = false; toggle.setAttribute('aria-expanded', 'true'); close.focus(); };
    const shut = () => { menu.hidden = true; toggle.setAttribute('aria-expanded', 'false'); toggle.focus(); };
    toggle.addEventListener('click', open);
    close.addEventListener('click', shut);
    document.addEventListener('keydown', e => { if (e.key === 'Escape' && !menu.hidden) shut(); });
  }

  document.querySelectorAll('.upload input[type=file]').forEach(input => {
    const box = input.closest('.upload');
    const preview = box.querySelector('img');
    const label = box.querySelector('[data-upload-label]');
    input.addEventListener('change', () => {
      const file = input.files[0];
      if (!file) return;
      box.classList.add('has-file');
      if (label) label.textContent = `${file.name} (${(file.size / 1024 / 1024).toFixed(2)} MB)`;
      if (preview) {
        preview.src = URL.createObjectURL(file);
        preview.hidden = false;
      }
      if (file.size > 10 * 1024 * 1024 && label) label.textContent += ' - terlalu besar, maksimal 10 MB';
    });
  });

  document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', e => {
      if (!confirm(form.dataset.confirm)) e.preventDefault();
    });
  });
})();
