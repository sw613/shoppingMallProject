// 로컬 스토리지에서 장바구니 데이터 가져오기
function getCart() {
    const cart = JSON.parse(localStorage.getItem('productIds')) || [];
    return cart.map(item => ({
        ...item,
        price: parseInt(item.price) || 0, // 숫자로 변환하여 기본값 0 설정
        selectedOption: item.selectedOption
            ? {
                ...item.selectedOption,
                optionPrice: parseInt(item.selectedOption.optionPrice) || 0 // 숫자로 변환
            }
            : null,
    }));
}

// 로컬 스토리지에 장바구니 데이터 저장
function saveCart(cart) {
    // 상품 합치기 로직
    const mergedCart = [];
    cart.forEach((item) => {
        const existingIndex = mergedCart.findIndex(
            (mergedItem) =>
                mergedItem.productId === item.productId &&
                JSON.stringify(mergedItem.selectedOption) === JSON.stringify(item.selectedOption)
        );

        if (existingIndex !== -1) {
            // 동일한 상품 및 옵션이 있으면 수량 합치기
            mergedCart[existingIndex].purchaseCounts += item.purchaseCounts;
        } else {
            // 새로 추가
            mergedCart.push(item);
        }
    });

    localStorage.setItem('productIds', JSON.stringify(mergedCart));
}


function renderCart() {
    const cart = getCart();
    const cartBody = document.getElementById('cart-body');

    if (!cartBody) {
        console.warn('cart-body 요소가 없습니다.');
        return;
    }

    cartBody.innerHTML = '';

    if (cart.length === 0) {
        cartBody.innerHTML = `<div class="empty-cart-message">장바구니가 비어 있습니다.</div>`;
        return;
    }

    cart.forEach((item, index) => {
        const optionPrice = item.selectedOption ? item.selectedOption.optionPrice : 0;
        const itemPrice = item.basePrice + optionPrice; // 기본 가격 + 옵션 가격
        const itemTotal = itemPrice * item.purchaseCounts;

        const row = document.createElement('div');
        row.className = 'cart-item';
        row.innerHTML = `
            <span><input type="checkbox" class="item-checkbox" data-index="${index}" ${
            item.checked ? 'checked' : ''
        } onchange="toggleItemCheck(${index})"></span>
            <span><img src="${item.image}" alt="${item.name}" style="width: 50px;"></span>
            <span>
                <strong>${item.name}</strong>
                ${item.selectedOption ? `<p class="option-display">[옵션: ${item.selectedOption.optionName}]</p>` : ''}
                <button class="option-button" onclick="openOptionModal(${index})">옵션변경</button>
            </span>
            <span>${itemPrice.toLocaleString()}원</span>
            <span class="purchaseCounts">
                <button class="quantity-btn" data-action="decrease" data-index="${index}">-</button>
                <span>${item.purchaseCounts}</span>
                <button class="quantity-btn" data-action="increase" data-index="${index}">+</button>
            </span>
            <span>${itemTotal.toLocaleString()}원</span>
            <span><button class="delete-button" onclick="removeItem(${index})">삭제</button></span>
        `;
        cartBody.appendChild(row);
    });

    // 수량 변경 이벤트 리스너 추가
    document.querySelectorAll('.quantity-btn').forEach((button) => {
        button.addEventListener('click', (event) => {
            const index = parseInt(event.target.dataset.index, 10);
            const action = event.target.dataset.action;

            const currentQuantity = cart[index].purchaseCounts;
            const newQuantity = action === 'increase' ? currentQuantity + 1 : currentQuantity - 1;
            updateQuantity(index, newQuantity);
        });
    });

    updateSelectedPrice(); // 금액 업데이트
}

function updateQuantity(index, newQuantity) {
    const cart = getCart();

    if (newQuantity <= 0) {
        alert('수량은 1 이상이어야 합니다.');
        return;
    }

    cart[index].purchaseCounts = newQuantity;
    saveCart(cart);
    renderCart(); // UI 업데이트
    updateSelectedPrice(); // 금액 업데이트
    updateTotal(); // 총합 업데이트
}


function applyOption() {
    const modal = document.getElementById('option-modal');
    const optionSelect = document.getElementById('option-select');
    const selectedOption = optionSelect.options[optionSelect.selectedIndex];
    const index = modal.getAttribute('data-index');

    if (!selectedOption || selectedOption.value === '0') {
        alert('옵션을 선택해주세요.');
        return;
    }

    const cart = getCart();
    const item = cart[index];

    // 옵션 변경
    item.selectedOption = {
        optionId: selectedOption.value,
        optionName: selectedOption.text.replace(/\(\+\d+원\)/g, ''),
        optionPrice: parseInt(selectedOption.dataset.price) || 0,
    };

    saveCart(cart);
    renderCart();
    closeOptionModal();
}



// 장바구니에 상품 추가
function addToCartHandler() {
    const addToCartBtn = document.getElementById('addToCartBtn');

    addToCartBtn.addEventListener('click', function () {
        const productId = addToCartBtn.getAttribute('data-product-id');
        const productName = document.querySelector('h3').textContent;
        const productImage = document.querySelector('#product-reqImage').src;
        const basicPrice = parseInt(document.getElementById('productBasicPrice').getAttribute('data-basic-price'));
        const quantity = parseInt(document.querySelector('.quantity-input').value);
        const selectedOptionElement = document.querySelector('.option-select');
        const selectedOption = selectedOptionElement.options[selectedOptionElement.selectedIndex];
        const optionId = selectedOption.value;
        const optionName = selectedOption.text.replace(/\(\+\d+원\)/g, ''); // 기존 옵션 이름에서 금액 제거
        const optionPrice = parseInt(selectedOption.getAttribute('data-price')) || 0;

        if (!optionId || optionId === '0') {
            alert('옵션을 선택해주세요.');
            return;
        }

        // 모든 옵션 값 가져오기
        const allOptions = Array.from(selectedOptionElement.options)
            .filter(option => option.value !== "[필수] 옵션을 선택해 주세요" && option.value)
            .map(option => ({
            optionId: option.value,
            optionName: option.text.replace(/\(\+\d+원\)/g, ''), // 옵션명에서 추가 금액 제거
            optionPrice: parseInt(option.getAttribute('data-price')) || 0,
        }));

        const cart = getCart();
        const existingProductIndex = cart.findIndex(
            item => item.productId === productId && item.selectedOption.optionId === optionId
        );

        const product = {
            productId,
            name: productName,
            basePrice: basicPrice, // 기본 가격 저장
            image: productImage,
            purchaseCounts: quantity,
            checked: true,
            selectedOption: {
                optionId,
                optionName,
                optionPrice, // 옵션 추가 가격만 저장
            },
            options: allOptions,
        };

        if (existingProductIndex !== -1) {
            // 동일 옵션이 장바구니에 있을 경우 수량만 증가
            cart[existingProductIndex].purchaseCounts += quantity;
        } else {
            // 새 항목 추가
            cart.push(product);
        }

        saveCart(cart);
        alert('상품이 장바구니에 추가되었습니다.');
        renderCart();
        updateSelectedPrice();
    });
}


function openOptionModal(index) {
    const cart = getCart();
    const item = cart[index];
    const modal = document.getElementById('option-modal');
    const optionSelect = document.getElementById('option-select');

    // 기존 옵션 초기화
    optionSelect.innerHTML = '';

    // item의 옵션 데이터를 가져오기
    const options = item.options || [];
    options.forEach(option => {
        const opt = document.createElement('option');
        opt.value = option.optionId;
        opt.textContent = option.optionName;
        opt.dataset.price = option.optionPrice || 0;
        optionSelect.appendChild(opt);
    });

    // 현재 선택된 옵션을 기본 선택값으로 설정
    if (item.selectedOption) {
        optionSelect.value = item.selectedOption.optionId;
    }

    // 버튼 위치 계산
    const button = document.querySelectorAll('.option-button')[index];
    const buttonRect = button.getBoundingClientRect();

    modal.style.top = `${buttonRect.bottom + 10}px`; // 버튼 아래 10px 위치
    modal.style.left = `${buttonRect.left}px`;
    modal.style.position = 'absolute'; // 화면 스크롤과 무관하게 고정 위치
    modal.style.display = 'block';

    // 현재 변경 중인 아이템의 인덱스를 저장
    modal.setAttribute('data-index', index);
}


// 체크박스 상태 토글
function toggleItemCheck(index) {
    const cart = getCart();
    cart[index].checked = !cart[index].checked;
    saveCart(cart);

    // 전체 선택 체크박스 상태 업데이트
    const selectAllCheckbox = document.getElementById('select-all');
    const allChecked = cart.every(item => item.checked);
    selectAllCheckbox.checked = allChecked;

    // 전체 선택 체크박스 상태 업데이트
    updateSelectAllCheckbox();

    // 금액 즉각 반영
    updateSelectedPrice();
    updateTotal();
}

function toggleSelectAll() {
    const selectAllCheckbox = document.getElementById('select-all');
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    const cart = getCart();

    // 전체 선택 상태에 따라 모든 항목의 체크 상태를 변경
    const isChecked = selectAllCheckbox.checked;
    cart.forEach((item, index) => {
        item.checked = isChecked;
        if (itemCheckboxes[index]) {
            itemCheckboxes[index].checked = isChecked;
        }
    });

    saveCart(cart);
    renderCart();
    updateSelectedPrice();
    updateTotal();
}


// 전체 선택 체크박스 상태 업데이트
function updateSelectAllCheckbox() {
    const cart = getCart();
    const selectAllCheckbox = document.getElementById('select-all');

    // 모든 항목이 체크된 경우만 전체 선택 체크박스 체크
    selectAllCheckbox.checked = cart.every(item => item.checked);
}

// 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', () => {
    renderCart();
    updateSelectedPrice();
    updateSelectAllCheckbox();

    // 전체 선택/해제 토글 이벤트 리스너
    const selectAllCheckbox = document.getElementById('select-all');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', toggleSelectAll);
    } else {
        console.error("전체 선택 체크박스를 찾을 수 없습니다.");
    }
});



// 모달 닫기
function closeOptionModal() {
    const modal = document.getElementById('option-modal');
    modal.style.display = 'none';
}

// 선택된 상품의 합계 및 배송비 업데이트
function updateSelectedPrice() {
    const cart = getCart();
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    let selectedTotal = 0;

    cart.forEach((item, index) => {
        const optionPrice = item.selectedOption ? item.selectedOption.optionPrice : 0;
        const itemPrice = item.basePrice + optionPrice; // 상품 가격 + 옵션 가격
        const itemTotal = itemPrice * item.purchaseCounts;

        if (itemCheckboxes[index]?.checked) {
            selectedTotal += itemTotal;
        }
    });

    const deliveryFee = selectedTotal >= 30000 ? 0 : selectedTotal > 0 ? 3000 : 0;
    const grandTotal = selectedTotal + deliveryFee;

    const totalPriceElement = document.getElementById('total-price');
    const deliveryFeeElement = document.getElementById('delivery-fee');
    const grandTotalElement = document.getElementById('grand-total');


    if (totalPriceElement) totalPriceElement.textContent = selectedTotal.toLocaleString();
    if (deliveryFeeElement) deliveryFeeElement.textContent = deliveryFee.toLocaleString();
    if (grandTotalElement) grandTotalElement.textContent = grandTotal.toLocaleString();


    // 총 상품금액, 총 배송비, 총 금액 업데이트 추가
    updateTotal();
}

function updateTotal() {
    const cart = getCart();
    let productPrice = 0;
    let shippingFee = 0;

    // 선택된 항목의 가격만 합산
    cart.forEach((item, index) => {
        const optionPrice = item.selectedOption ? item.selectedOption.optionPrice : 0;
        const itemPrice = item.basePrice + optionPrice;
        const itemCheckbox = document.querySelectorAll('.item-checkbox')[index];

        if (itemCheckbox && itemCheckbox.checked) {
            productPrice += itemPrice * item.purchaseCounts;
        }
    });

    // 배송비 계산
    shippingFee = productPrice >= 30000 ? 0 : productPrice > 0 ? 3000 : 0;

    const totalPrice = productPrice + shippingFee;

    // UI 업데이트
    const productPriceElement = document.getElementById('product-price');
    const shippingFeeElement = document.getElementById('shipping-fee');
    const totalPriceElement = document.getElementById('total-price2');

    if (productPriceElement) productPriceElement.textContent = `${productPrice.toLocaleString()}원`;
    if (shippingFeeElement) shippingFeeElement.textContent = `+ ${shippingFee.toLocaleString()}원`;
    if (totalPriceElement) totalPriceElement.textContent = `= ${totalPrice.toLocaleString()}원`;
}

function calculateProductPrice() {
    const cart = getCart();
    return cart.reduce((sum, item) => {
        const optionPrice = item.selectedOption ? item.selectedOption.optionPrice : 0;
        const itemPrice = item.basePrice + optionPrice;
        return sum + (itemPrice * item.purchaseCounts);
    }, 0);
}

function calculateShippingFee() {
    const productPrice = calculateProductPrice();
    return productPrice >= 30000 ? 0 : 3000; // 3만 원 이상 무료배송
}

document.addEventListener('DOMContentLoaded', () => {
    updateTotal();
});

// function clearCart() {
//     // 장바구니 비우기 로직
//     alert('장바구니가 비워졌습니다!');
//     // 총합 초기화
//     document.getElementById('product-price').textContent = '0원';
//     document.getElementById('shipping-fee').textContent = '+ 0원';
//     document.getElementById('total-price').textContent = '= 0원';
// }
//
// // 페이지 로드 후 초기 업데이트
// document.addEventListener('DOMContentLoaded', () => {
//     updateTotal();
// });


document.addEventListener('DOMContentLoaded', () => {
    // 버튼 이벤트 리스너 추가
    document.getElementById('order-all').addEventListener('click', () => {
        window.location.href = 'http://34.47.78.233/view/payment/orders';
    });

    document.getElementById('order-selected').addEventListener('click', () => {

        const curCart = getCart(); // 원래 장바구니 카트
        const newCart = [];  // 선택된 카트
        const leaveCart = [];// 선택안된 카드


        curCart.forEach((item, index) => {
            if (item.checked) {
                newCart.push(item);
            } else {
                leaveCart.push(item);
            }
        });

        localStorage.setItem('productIds', JSON.stringify(newCart));
        localStorage.setItem('productIds2', JSON.stringify(leaveCart));

        window.location.href = 'http://34.47.78.233/view/payment/orders'; // 필요에 따라 다른 URL로 설정 가능
    });
});


// 상품 삭제
function removeItem(index) {
    const cart = getCart();
    cart.splice(index, 1);
    saveCart(cart);
    renderCart();
}


function deleteAllItems() {
    const confirmation = confirm("정말로 장바구니의 모든 항목을 삭제하시겠습니까?");
    if (confirmation) {
        const cart = [];
        saveCart(cart);
        renderCart();
        document.getElementById('select-all').checked = false; // 전체 선택 체크박스 해제
        updateSelectedPrice();
        // alert("장바구니의 모든 항목이 삭제되었습니다.");
    }
}

function deleteSelectedItems() {
    const cart = getCart();
    const updatedCart = cart.filter((item, index) => {
        const itemCheckbox = document.querySelectorAll('.item-checkbox')[index];
        return !(itemCheckbox && itemCheckbox.checked);
    });

    saveCart(updatedCart);
    renderCart();

    const allUnchecked = updatedCart.length === 0 || updatedCart.every(item => !item.checked);
    document.getElementById('select-all').checked = !allUnchecked; // 체크된 항목 없으면 전체 선택 체크 해제

    updateSelectedPrice();
    alert("선택된 항목이 삭제되었습니다.");
}


// 초기화 및 이벤트 등록
document.addEventListener('DOMContentLoaded', () => {
    renderCart();
    updateSelectedPrice();
    addToCartHandler();
    window.toggleItemCheck = toggleItemCheck;
    window.toggleSelectAll = toggleSelectAll;
    window.applyOption = applyOption;
});



