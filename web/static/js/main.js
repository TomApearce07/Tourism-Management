document.addEventListener('DOMContentLoaded', function () {

    /* 1. Ẩn/hiện mật khẩu (login.jsp, register.jsp) */
    document.querySelectorAll('.toggle-pw').forEach(function (b) {
        b.addEventListener('click', function () {
            var i = document.getElementById(b.dataset.target);
            var show = i.type === 'password';
            i.type = show ? 'text' : 'password';
            b.innerHTML = '<i class="bi ' + (show ? 'bi-eye' : 'bi-eye-slash') + '"></i>';
        });
    });

    /* 2. Register: kiểm tra mật khẩu nhập lại có khớp không */
    var pw = document.getElementById('password');
    var cpw = document.getElementById('confirmPassword');
    if (pw && cpw) {
        cpw.form.addEventListener('submit', function (e) {
            if (pw.value !== cpw.value) {
                e.preventDefault();
                cpw.setCustomValidity('Mật khẩu nhập lại không khớp');
                cpw.reportValidity();
            }
        });
        cpw.addEventListener('input', function () {
            cpw.setCustomValidity('');
        });
    }

    /* 3. Tour detail: nút tăng/giảm số khách + tính tổng tiền */
    var form = document.getElementById('bookForm');
    if (form) {
        var price = parseInt(form.dataset.price, 10);
        var max = Math.max(parseInt(form.dataset.max, 10), 1);
        var n = parseInt(form.dataset.default, 10);
        var render = function () {
            document.getElementById('guestsText').textContent = n;
            document.getElementById('totalGuests').textContent = n;
            document.getElementById('guestsInput').value = n;
            document.getElementById('totalPrice').textContent = (price * n).toLocaleString('en-US') + ' VND';
        };
        document.getElementById('btnMinus').addEventListener('click', function () {
            if (n > 1) {
                n--;
                render();
            }
        });
        document.getElementById('btnPlus').addEventListener('click', function () {
            if (n < max) {
                n++;
                render();
            }
        });
    }

    /* 4. Checkout: tô viền ô thanh toán đang chọn */
    var payOptions = document.querySelectorAll('.pay-option');
    function syncPay() {
        payOptions.forEach(function (o) {
            o.classList.toggle('selected', o.querySelector('input').checked);
        });
    }
    payOptions.forEach(function (o) {
        o.querySelector('input').addEventListener('change', syncPay);
    });
    syncPay();

    /* 5. <select data-autosubmit>: đổi giá trị là tự động gửi form.
          Dùng ủy quyền sự kiện (gắn vào document) để vẫn chạy với nội dung được tự động làm mới (live-refresh.js). */
    document.addEventListener('change', function (e) {
        var s = e.target;
        if (s && s.matches && s.matches('select[data-autosubmit]')) {
            s.form.submit();
        }
    });

    /* 6. <form data-confirm="...">: hiển thị popup hỏi xác nhận trước khi gửi (cũng dùng ủy quyền sự kiện) */
    document.addEventListener('submit', function (e) {
        var f = e.target;
        if (f && f.matches && f.matches('form[data-confirm]')) {
            if (!window.confirm(f.dataset.confirm))
                e.preventDefault();
        }
    });

    /* 7. Tự động mở modal Create New Tour nếu có tham số trên URL */
    var modalEl = document.getElementById('createTourModal');
    if (modalEl && window.bootstrap && /[?&]create=1/.test(location.search)) {
        new bootstrap.Modal(modalEl).show();
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const scrollPos = sessionStorage.getItem("scrollPosition");
    if (scrollPos) {
        window.scrollTo(0, parseInt(scrollPos, 10));
        sessionStorage.removeItem("scrollPosition");
    }

    // Ghi nhớ vị trí cuộn khi gửi bất kỳ form nào (ủy quyền sự kiện nên form mới xuất hiện sau khi làm mới cũng được tính)
    document.addEventListener("submit", function () {
        sessionStorage.setItem("scrollPosition", window.scrollY);
    });
});

function toggleUserDropdown(event) {
    event.stopPropagation();
    const menu = document.getElementById("userDropdownMenu");
    if (menu) {
        menu.classList.toggle("show");
    }
}

window.addEventListener("click", function () {
    const menu = document.getElementById("userDropdownMenu");
    if (menu && menu.classList.contains("show")) {
        menu.classList.remove("show");
    }
});