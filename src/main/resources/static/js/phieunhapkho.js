(function () {
  const tableBody = document.getElementById('phieuNhapBody');
  const addRowBtn = document.getElementById('addRowBtn');
  const template = document.getElementById('phieuNhapRowTemplate');
  const grandTotalEl = document.getElementById('grandTotal');

  if (!tableBody || !addRowBtn || !template || !grandTotalEl) {
    return;
  }

  function moneyFormat(value) {
    const safe = Number.isFinite(value) ? value : 0;
    return new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 0 }).format(safe) + ' đ';
  }

  function parseNumber(input) {
    const value = Number.parseFloat(input?.value);
    return Number.isFinite(value) ? value : 0;
  }

  function getRows() {
    return Array.from(tableBody.querySelectorAll('.phieu-nhap-row'));
  }

  function calculateRow(row) {
    const soLuongInput = row.querySelector('.so-luong-input');
    const donGiaInput = row.querySelector('.don-gia-input');
    const totalEl = row.querySelector('.row-total');
    const soLuong = parseNumber(soLuongInput);
    const donGia = parseNumber(donGiaInput);
    const thanhTien = soLuong * donGia;
    if (totalEl) {
      totalEl.textContent = moneyFormat(thanhTien);
    }
    return thanhTien;
  }

  function recalculateAll() {
    const grandTotal = getRows().reduce((sum, row) => sum + calculateRow(row), 0);
    grandTotalEl.textContent = moneyFormat(grandTotal);
  }

  function reindexRows() {
    getRows().forEach((row, index) => {
      row.dataset.index = String(index);
      row.querySelectorAll('input, select').forEach((el) => {
        if (el.name) {
          el.name = el.name.replace(/danhSachChiTiet\[\d+\]/g, `danhSachChiTiet[${index}]`);
        }
        if (el.id) {
          el.id = el.id.replace(/_\d+$/, `_${index}`);
        }
      });
    });
  }

  function bindRowEvents(row) {
    const soLuongInput = row.querySelector('.so-luong-input');
    const donGiaInput = row.querySelector('.don-gia-input');
    const removeBtn = row.querySelector('.remove-row-btn');

    if (soLuongInput) {
      soLuongInput.addEventListener('input', recalculateAll);
      soLuongInput.addEventListener('change', recalculateAll);
    }
    if (donGiaInput) {
      donGiaInput.addEventListener('input', recalculateAll);
      donGiaInput.addEventListener('change', recalculateAll);
    }
    if (removeBtn) {
      removeBtn.addEventListener('click', () => {
        const rows = getRows();
        if (rows.length <= 1) {
          const firstRow = rows[0];
          if (firstRow) {
            const select = firstRow.querySelector('.nguyen-lieu-select');
            const quantity = firstRow.querySelector('.so-luong-input');
            const price = firstRow.querySelector('.don-gia-input');
            if (select) select.selectedIndex = 0;
            if (quantity) quantity.value = '1';
            if (price) price.value = '0';
            recalculateAll();
          }
          return;
        }

        row.remove();
        reindexRows();
        recalculateAll();
      });
    }
  }

  function addRow() {
    const nextIndex = getRows().length;
    const html = template.innerHTML.replace(/__INDEX__/g, String(nextIndex));
    const wrapper = document.createElement('tbody');
    wrapper.innerHTML = html.trim();
    const newRow = wrapper.firstElementChild;
    if (!newRow) {
      return;
    }
    tableBody.appendChild(newRow);
    reindexRows();
    bindRowEvents(newRow);
    recalculateAll();
  }

  getRows().forEach((row) => bindRowEvents(row));

  addRowBtn.addEventListener('click', addRow);
  tableBody.addEventListener('input', recalculateAll);
  tableBody.addEventListener('change', recalculateAll);

  recalculateAll();
})();
