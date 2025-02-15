///////////////////////////////////////////////////////////////////////////
/////////////////////////////// 상품페이지 ///////////////////////////////////
///////////////////////////////////////////////////////////////////////////

document.addEventListener("DOMContentLoaded", function () {
    // 필요한 DOM 요소
    const selectElement = document.querySelector(".option-select");
    const selectedProductDiv = document.getElementById("selectedProductDiv");
    const totalPriceElement = document.getElementById("totalPrice");
    const addToCartBtn = document.getElementById("addToCartBtn");
    const basicPrice = document.getElementById("productBasicPrice").getAttribute('data-basic-price');

    // 선택된 옵션 저장
    let selectedOption = null; // 현재 선택된 옵션 정보

    // 옵션 변경 이벤트
    selectElement.addEventListener("change", function () {
        const selectedOptionElement = this.options[this.selectedIndex];
        const optionId = selectedOptionElement.value;
        const optionName = selectedOptionElement.textContent.trim();
        const optionPrice = parseInt(basicPrice) + (parseInt(selectedOptionElement.dataset.price, 10) || 0);

        if (!optionId || optionId === "[필수] 옵션을 선택해 주세요") {
            return;
        }

        // 기존 선택된 옵션이 있으면 경고 메시지
        if (selectedOption) {
            alert("이미 옵션이 선택되었습니다. 기존 옵션을 삭제 후 다시 선택해주세요.");
            return;
        }

        // 선택된 옵션 저장
        selectedOption = { optionId, optionName, optionPrice, quantity: 1 };

        // 선택된 옵션 표시
        const optionRow = document.createElement("div");
        optionRow.className = "selected-option border-top border-bottom";
        optionRow.dataset.optionId = optionId;
        optionRow.innerHTML = `
            <div class="row align-items-center mt-2 mb-2">
                <div class="col-4">
                    <span class="fw-bold">${optionName}</span>
                </div>
                <div class="col-4 d-flex align-items-center justify-content-center">
                    <button class="btn btn-outline-secondary btn-sm mx-1 decrease-qty">-</button>
                    <input type="text" value="1" class="form-control form-control-sm text-center mx-1 quantity-input" style="width: 50px;" readonly>
                    <button class="btn btn-outline-secondary btn-sm mx-1 increase-qty">+</button>
                </div>
                <div class="col-3 text-end">
                    <span class="fw-bold option-price">${optionPrice.toLocaleString()}원</span>
                </div>
                <div class="col-1 text-end">
                    <button class="btn btn-danger btn-sm remove-option">X</button>
                </div>
            </div>
        `;
        selectedProductDiv.appendChild(optionRow);

        // 총 가격 갱신
        updateTotalPrice();

        // 수량 증가 이벤트
        optionRow.querySelector(".increase-qty").addEventListener("click", function () {
            changeQuantity(1);
        });

        // 수량 감소 이벤트
        optionRow.querySelector(".decrease-qty").addEventListener("click", function () {
            changeQuantity(-1);
        });

        // 옵션 삭제 이벤트
        optionRow.querySelector(".remove-option").addEventListener("click", function () {
            removeOption();
        });
    });

    // 수량 변경 함수
    function changeQuantity(delta) {
        if (!selectedOption) return;

        selectedOption.quantity += delta;

        // 최소 수량 제한
        if (selectedOption.quantity < 1) {
            selectedOption.quantity = 1;
            alert("최소 수량은 1개입니다.");
            return;
        }

        // 업데이트된 수량 및 가격 반영
        const optionRow = selectedProductDiv.querySelector(`[data-option-id="${selectedOption.optionId}"]`);
        optionRow.querySelector(".quantity-input").value = selectedOption.quantity;
        const updatedPrice = selectedOption.optionPrice * selectedOption.quantity;
        optionRow.querySelector(".option-price").textContent = `${updatedPrice.toLocaleString()}원`;

        // 총 가격 갱신
        updateTotalPrice();
    }

    // 옵션 삭제 함수
    function removeOption() {
        const optionRow = selectedProductDiv.querySelector(`[data-option-id="${selectedOption.optionId}"]`);
        if (optionRow) {
            optionRow.remove();
        }
        selectedOption = null; // 선택된 옵션 초기화
        updateTotalPrice();
    }

    // 총 가격 갱신 함수
    function updateTotalPrice() {
        let totalPrice = 0;
        if (selectedOption) {
            totalPrice = selectedOption.optionPrice * selectedOption.quantity;
        }
        totalPriceElement.textContent = `${totalPrice.toLocaleString()} 원`;
    }

    // 장바구니 추가 버튼 클릭 이벤트
    addToCartBtn.addEventListener("click", function () {
        const productId = this.dataset.productId;

        if (!selectedOption) {
            alert("옵션을 선택해주세요.");
            return;
        }

        // 선택된 옵션 정보 서버로 전송
        const payload = {
            productId: productId,
            option: selectedOption
        };

        console.log("장바구니 추가 요청 데이터:", payload);
        // 여기서 AJAX 요청이나 fetch를 사용해 서버로 데이터 전송 가능
    });
});


///////////////////////////////////////////////////////////////////////////
/////////////////////////////// Review ////////////////////////////////////
///////////////////////////////////////////////////////////////////////////

document.addEventListener('DOMContentLoaded', function () {
    // 리뷰 수정 버튼 클릭 시
    document.querySelectorAll('.edit-review-btn').forEach(button => {
        button.addEventListener('click', function () {
            const reviewId = this.getAttribute('data-review-id');  // 클릭된 버튼에서 reviewId 가져오기
            const reviewContent = this.getAttribute('data-review-content');  // 리뷰 내용
            const reviewScore = this.getAttribute('data-review-score');  // 리뷰 평점
            const productId = this.getAttribute('data-product-id');  // 상품 ID

            // 수정 모드로 설정
            const isEditMode = true;
            document.getElementById('reviewForm').reset();  // 폼 초기화
            document.getElementById('review-productId').value = productId;
            document.getElementById('review-detail').value = reviewContent;

            // 해당 리뷰의 평점을 라디오 버튼으로 설정
            const scoreRadios = document.querySelectorAll('input[name="score"]');
            scoreRadios.forEach(radio => {
                radio.checked = (radio.value === reviewScore);  // 리뷰의 평점에 맞게 선택
            });

            // 리뷰 수정 버튼을 활성화
            document.getElementById('submitReview').disabled = false;
            document.getElementById('submitReview').onclick = function () {
                submitReview(reviewId, isEditMode);
            };
        });
    });

    // 리뷰 작성하기 버튼 클릭 시
    const writeReviewButton = document.getElementById('show-reviewModal-btn');
    if (writeReviewButton) {
        writeReviewButton.addEventListener('click', function () {
            // 리뷰 작성 폼 초기화
            document.getElementById('reviewForm').reset();
            document.getElementById('review-productId').value = this.getAttribute('data-product-id');
            document.getElementById('review-detail').value = '';
            document.getElementById('submitReview').disabled = false;
            document.getElementById('submitReview').onclick = function () {
                submitReview(null, false);  // 새 리뷰 작성 모드
            };
        });
    }
});

// 리뷰 제출 함수
function submitReview(reviewId, isEditMode) {
    const form = document.getElementById('reviewForm');
    const formData = new FormData(form);
    const url = isEditMode ? '/review/update/' + reviewId : '/review/create';
    // const method = isEditMode ? 'PUT' : 'POST';

    fetch(url, {
        method: 'POST',
        body: formData,
    })
        .then(response => {
            if (response.ok) {

                alert(isEditMode ? '리뷰가 성공적으로 수정되었습니다!' : '리뷰가 성공적으로 작성되었습니다!');
                window.location.href = response.url;
            } else {
                throw new Error('서버 응답 실패');
            }
        })
        .catch(error => {
            alert(isEditMode ? '리뷰 수정 중 오류가 발생했습니다.' : '리뷰 작성 중 오류가 발생했습니다.');
        });
}

// 리뷰 버튼 리스너등록
document.addEventListener('DOMContentLoaded', () => {
    const deleteButtons = document.querySelectorAll('.delete-review-btn');
    deleteButtons.forEach(button => {
        button.addEventListener('click', () => {
            const reviewId = button.getAttribute('data-review-id');
            confirmDeleteReview(reviewId);
        });
    });
});

// 리뷰 삭제
function confirmDeleteReview(reviewId) {
    // 삭제 여부 확인
    if (confirm('정말 삭제하시겠습니까?')) {
        // 삭제 요청 보내기
        fetch(`/review/delete/${reviewId}`, {
            method: 'POST',
        })
            .then(response => {
                if (response.ok) {
                    alert('리뷰가 성공적으로 삭제되었습니다.');
                    location.reload(); // 페이지 새로고침
                } else {
                    throw new Error('리뷰 삭제 실패');
                }
            })
            .catch(error => {
                console.error('삭제 오류:', error);
                alert('리뷰 삭제 중 오류가 발생했습니다.');
            });
    }
}


// 리뷰 데이터 검증
document.addEventListener("DOMContentLoaded", function () {
    const reviewInput = document.getElementById("review-detail");
    const submitButton = document.getElementById("submitReview");

    // 평점 선택을 체크하는 함수
    function isRatingValid() {
        const selectedScore = document.querySelector('input[name="score"]:checked');
        return selectedScore && selectedScore.value && selectedScore.value >= 1 && selectedScore.value <= 10;
    }

    // 리뷰 내용 검증 함수
    function isReviewValid() {
        const reviewText = reviewInput.value.trim();
        return reviewText.length >= 10;  // 최소 10글자
    }

    reviewInput.addEventListener("input", function () {
        if (isReviewValid() && isRatingValid()) {
            submitButton.disabled = false;
        } else {
            submitButton.disabled = true;
        }
    });

    // 평점 선택이 바뀔 때마다 검증
    document.querySelectorAll('input[name="score"]').forEach(function (radio) {
        radio.addEventListener("change", function () {
            if (isReviewValid() && isRatingValid()) {
                submitButton.disabled = false;
            } else {
                submitButton.disabled = true;
            }
        });
    });
});


///////////////////////////////////////////////////////////////////////////
/////////////////////////////// Q & A /////////////////////////////////////
///////////////////////////////////////////////////////////////////////////



// 문의 검증
document.addEventListener("DOMContentLoaded", function () {
    const commentInput = document.getElementById("comment-detail");
    const submitButton = document.getElementById("submitComment");

    // 코멘트 내용 검증 함수
    function isCommentValid() {
        const commentText = commentInput.value.trim();
        return commentText.length >= 10;  // 최소 10글자
    }

    commentInput.addEventListener("input", function () {
        submitButton.disabled = !isCommentValid();
    });

});

// 답변 검증
document.addEventListener("DOMContentLoaded", function () {
    const recommentInput = document.getElementById("recomment-detail");
    const submitButton = document.getElementById("submitRecomment");

    // 코멘트 내용 검증 함수
    function isRecommentValid() {
        const recommentText = recommentInput.value.trim();
        return recommentText.length >= 10;  // 최소 10글자
    }

    recommentInput.addEventListener("input", function () {
        submitButton.disabled = !isRecommentValid();
    });
});



// 문의
document.addEventListener('DOMContentLoaded', function () {
    // 문의 수정 버튼 클릭 시
    document.querySelectorAll('.edit-comment-btn').forEach(button => {
        button.addEventListener('click', function () {
            const commentId = this.getAttribute('data-comment-id');  // 클릭된 버튼에서 reviewId 가져오기
            const commentContent = this.getAttribute('data-comment-content');  // 문의 내용
            const commentIsSecret = this.getAttribute('data-comment-isSecret');  // 비밀글 여부
            const productId = this.getAttribute('data-product-id');  // 상품 ID

            // 수정 모드로 설정
            const isEditMode = true;
            document.getElementById('commentForm').reset();  // 폼 초기화
            document.getElementById('comment-productId').value = productId;
            document.getElementById('comment-detail').value = commentContent;
            document.getElementById('comment-isSecret').value = commentIsSecret;

            // 문의 수정 버튼을 활성화
            document.getElementById('submitComment').disabled = false;
            document.getElementById('submitComment').onclick = function () {
                submitComment(commentId, isEditMode);
            };
        });
    });

    // 문의 작성하기 버튼 클릭 시
    const writeCommentButton = document.getElementById('show-commentModal-btn');
    if (writeCommentButton) {
        writeCommentButton.addEventListener('click', function () {
            // 문의 작성 폼 초기화
            document.getElementById('commentForm').reset();
            document.getElementById('comment-productId').value = this.getAttribute('data-product-id');
            document.getElementById('comment-detail').value = '';
            document.getElementById('submitComment').disabled = false;
            document.getElementById('submitComment').onclick = function () {
                submitComment(null, false);  // 새 문의 작성 모드
            };
        });
    }
});

// 문의 제출 함수
function submitComment(commentId, isEditMode) {
    const form = document.getElementById('commentForm');
    const formData = new FormData(form);
    const url = isEditMode ? '/comment/update/' + commentId : '/comment/create';

    fetch(url, {
        method: 'POST',
        body: formData,
    })
        .then(response => {
            if (response.ok) {

                alert(isEditMode ? '문의가 성공적으로 수정되었습니다!' : '문의가 성공적으로 작성되었습니다!');
                window.location.href = response.url;
            } else {
                throw new Error('서버 응답 실패');
            }
        })
        .catch(error => {
            alert(isEditMode ? '문의 수정 중 오류가 발생했습니다.' : '문의 작성 중 오류가 발생했습니다.');
        });
}

// 삭제 버튼 리스너등록
document.addEventListener('DOMContentLoaded', () => {
    const deleteButtons = document.querySelectorAll('.delete-comment-btn');
    deleteButtons.forEach(button => {
        button.addEventListener('click', () => {
            const commentId = button.getAttribute('data-comment-id');
            confirmDeleteComment(commentId);
        });
    });
});

// 문의 삭제
function confirmDeleteComment(commentId) {
    // 삭제 여부 확인
    if (confirm('정말 삭제하시겠습니까?')) {
        // 삭제 요청 보내기
        fetch(`/comment/delete/${commentId}`, {
            method: 'POST',
        })
            .then(response => {
                if (response.ok) {
                    alert('문의가 성공적으로 삭제되었습니다.');
                    location.reload(); // 페이지 새로고침
                } else {
                    throw new Error('문의 삭제 실패');
                }
            })
            .catch(error => {
                console.error('삭제 오류:', error);
                alert('문의 삭제 중 오류가 발생했습니다.');
            });
    }
}

// 답변 토글 기능 추가
document.addEventListener("DOMContentLoaded", function () {
    const toggleAnswerBtns = document.querySelectorAll(".toggle-answer-btn");

    toggleAnswerBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
            // 버튼과 동일한 row에서 다음 형제 row(answer-row)를 찾음
            const answerRow = btn.closest(".row").nextElementSibling;

            // 답변 영역 표시/숨김 토글
            if (answerRow.style.display === "none" || answerRow.style.display === "") {
                answerRow.style.display = "flex"; // 답변 영역 표시
            } else {
                answerRow.style.display = "none"; // 답변 영역 숨김
            }
        });
    });
});

// 답변
document.addEventListener('DOMContentLoaded', function () {
    // 답변 수정 버튼 클릭 시
    document.querySelectorAll('.edit-recomment-btn').forEach(button => {
        button.addEventListener('click', function () {
            const recommentId = this.getAttribute('data-recomment-id');  // 클릭된 버튼에서 recommentId 가져오기
            const commentId = this.getAttribute('data-comment-id');  // 클릭된 버튼에서 commentId 가져오기
            const productId = this.getAttribute('data-product-id');  // 상품 ID
            const commentContent = this.getAttribute('data-comment-content');  // 답변 내용

            // 수정 모드로 설정
            const isEditMode = true;
            document.getElementById('recommentForm').reset();  // 폼 초기화
            document.getElementById('recomment-productId').value = productId;
            document.getElementById('recomment-commentId').value = commentId;
            document.getElementById('recomment-detail').value = commentContent;

            // 답변 수정 버튼을 활성화
            document.getElementById('submitRecomment').disabled = false;
            document.getElementById('submitRecomment').onclick = function () {
                submitRecomment(recommentId, isEditMode);
            };
        });
    });

    // 답변 작성하기 버튼 클릭 시
    const writeRecommentButton = document.getElementById('show-recommentModal-btn');
    if (writeRecommentButton) {
        writeRecommentButton.addEventListener('click', function () {
            // 답변 작성 폼 초기화
            document.getElementById('recommentForm').reset();
            document.getElementById('recomment-productId').value = this.getAttribute('data-product-id');
            document.getElementById('recomment-commentId').value = this.getAttribute('data-comment-id');
            document.getElementById('recomment-detail').value = '';
            document.getElementById('submitRecomment').disabled = false;
            document.getElementById('submitRecomment').onclick = function () {
                submitRecomment(null, false);  // 새 답변 작성 모드
            };
        });
    }
});

// 답변 제출 함수
function submitRecomment(recommentId, isEditMode) {
    const form = document.getElementById('recommentForm');
    const formData = new FormData(form);
    const url = isEditMode ? '/recomment/update/' + recommentId : '/recomment/create';

    fetch(url, {
        method: 'POST',
        body: formData,
    })
        .then(response => {
            if (response.ok) {
                alert(isEditMode ? '답변이 성공적으로 수정되었습니다!' : '답변이 성공적으로 작성되었습니다!');
                window.location.href = response.url;
            } else {
                throw new Error('서버 응답 실패');
            }
        })
        .catch(error => {
            alert(isEditMode ? '답변 수정 중 오류가 발생했습니다.' : '답변 작성 중 오류가 발생했습니다.');
        });
}


