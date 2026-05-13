(function () {
  const tableBody = document.getElementById('phieuXuatBody');
  const addRowBtn = document.getElementById('addRowBtn');
  const template = document.getElementById('phieuXuatRowTemplate');
  if (!tableBody || !addRowBtn || !template) {
    return;
  }

  const rowsSelector = '.phieu-xuat-row';
  const moneyFormat = (value) => new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 0 }).format(Number.isFinite(value) ? value : 0);

  function getRows() {
    return Array.from(tableBody.querySelectorAll(rowsSelector));
  }

  function parseNumber(input) {
    const value = Number.parseFloat(input?.value);
    return Number.isFinite(value) ? value : 0;
  }

  function getStockFromSelect(select) {
    const option = select?.selectedOptions?.[0];
    if (!option) {
      return 0;
    }
    const stock = Number.parseInt(option.dataset.stock || '0', 10);
    return Number.isFinite(stock) ? stock : 0;
  }

  function updateRow(row) {
    const select = row.querySelector('.nguyen-lieu-select');
    const quantityInput = row.querySelector('.so-luong-input');
    const stockCell = row.querySelector('.stock-cell');
    const stock = getStockFromSelect(select);
    if (stockCell) {
      stockCell.textContent = select && select.value ? moneyFormat(stock) : '---';
    }
    if (quantityInput) {
      quantityInput.max = stock > 0 ? String(stock) : '';
      if (quantityInput.value && stock > 0 && Number.parseInt(quantityInput.value, 10) > stock) {
        quantityInput.value = String(stock);
      }
    }
    const quantity = parseNumber(quantityInput);
    const total = quantity;
    const totalEl = row.querySelector('.row-total');
    if (totalEl) {
      totalEl.textContent = moneyFormat(total);
    }
    return total;
  }

  function recalculateAll() {
    getRows().forEach((row) => updateRow(row));
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

  function bindRow(row) {
    const select = row.querySelector('.nguyen-lieu-select');
    const quantityInput = row.querySelector('.so-luong-input');
    const removeBtn = row.querySelector('.remove-row-btn');
    if (select) {
      select.addEventListener('change', () => {
        updateRow(row);
      });
    }
    if (quantityInput) {
      quantityInput.addEventListener('input', () => {
        updateRow(row);
      });
      quantityInput.addEventListener('change', () => {
        updateRow(row);
      });
    }
    if (removeBtn) {
      removeBtn.addEventListener('click', () => {
        const rows = getRows();
        if (rows.length <= 1) {
          const firstRow = rows[0];
          if (!firstRow) {
            return;
          }
          const firstSelect = firstRow.querySelector('.nguyen-lieu-select');
          const firstQuantity = firstRow.querySelector('.so-luong-input');
          if (firstSelect) {
            firstSelect.selectedIndex = 0;
          }
          if (firstQuantity) {
            firstQuantity.value = '';
          }
          recalculateAll();
          return;
        }
        row.remove();
        reindexRows();
        recalculateAll();
      });
    }
  }

  addRowBtn.addEventListener('click', () => {
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
    bindRow(newRow);
    recalculateAll();
  });

  getRows().forEach((row) => bindRow(row));
  recalculateAll();
})();
