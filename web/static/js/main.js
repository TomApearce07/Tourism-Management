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
        var depSel = document.getElementById('departureSelect');   // chọn ngày khởi hành (nếu có)
        // Số khách tối đa = số chỗ còn lại của NGÀY đang chọn (không phải cả tour)
        var getMax = function () {
            if (depSel && depSel.selectedOptions.length) {
                var m = parseInt(depSel.selectedOptions[0].dataset.seats, 10);
                if (m > 0) {
                    return m;
                }
            }
            return Math.max(parseInt(form.dataset.max, 10), 1);
        };
        var max = getMax();
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
        // Ghi chú "cần ≥ N khách, hiện có M" theo ngày đang chọn
        var updateReq = function () {
            var out = document.getElementById('depReqDetail');
            if (!out || !depSel || !depSel.selectedOptions.length) {
                return;
            }
            var o = depSel.selectedOptions[0].dataset;
            out.textContent = '(Ngày ' + o.label + ': cần ≥ ' + o.required + ' khách, hiện đã có ' + o.registered + '.)';
        };
        // Tự cập nhật số chỗ mỗi 5 giây (không cần tải lại trang). Server vẫn kiểm tra lại khi đặt.
        var pollSeats = function () {
            if (document.hidden || !form.dataset.seatsApi) {
                return;
            }
            fetch(form.dataset.seatsApi, {cache: 'no-store', credentials: 'same-origin'}).then(function (r) {
                return r.ok ? r.json() : null;
            }).then(function (list) {
                if (!list) {
                    return;
                }
                var byId = {};
                list.forEach(function (d) {
                    byId[d.departureId] = d;
                });
                Array.prototype.forEach.call(depSel.options, function (opt) {
                    var d = byId[opt.value];
                    var seats = d ? d.seatsLeft : 0;
                    opt.dataset.seats = seats;
                    if (d) {
                        opt.dataset.required = d.required;
                        opt.dataset.registered = d.registered;
                    }
                    opt.disabled = seats <= 0;
                    opt.textContent = opt.dataset.label + ' · ' + (seats > 0 ? 'còn ' + seats + ' chỗ' : 'Hết chỗ');
                });
                if (depSel.selectedOptions.length && depSel.selectedOptions[0].disabled) {
                    var firstOk = Array.prototype.filter.call(depSel.options, function (o) {
                        return !o.disabled;
                    })[0];
                    if (firstOk) {
                        depSel.value = firstOk.value;
                    }
                }
                max = getMax();
                if (n > max) {
                    n = max;
                }
                render();
                updateReq();
            }).catch(function () { /* mất mạng tạm thời: bỏ qua */ });
        };
        if (depSel) {
            setInterval(pollSeats, 5000);
            updateReq();
            depSel.addEventListener('change', updateReq);
            depSel.addEventListener('change', function () {
                max = getMax();
                if (n > max) {      // đổi sang ngày ít chỗ hơn: hạ số khách xuống
                    n = max;
                }
                render();
            });
            max = getMax();
            if (n > max) {
                n = max;
            }
            render();
        }
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

    /* 4b. Checkout: danh sách hành khách.
           - Hành khách 1 = người đại diện: đồng bộ 2 chiều với ô trong hộp thoại thanh toán.
           - Hiện nhãn Trẻ em / Người lớn theo ngày sinh so với NGÀY KHỞI HÀNH; CCCD chỉ bắt buộc với người lớn.
           (Server vẫn kiểm tra lại tất cả, đây chỉ là gợi ý cho người dùng.) */
    var paxSection = document.getElementById('paxSection');
    if (paxSection) {
        var depDate = new Date(paxSection.dataset.depdate + 'T00:00:00');
        ['name', 'id', 'phone'].forEach(function (key) {
            var a = paxSection.querySelector('[data-rep="' + key + '"]');
            var b = document.querySelector('[data-rep-src="' + key + '"]');
            if (!a || !b) {
                return;
            }
            if (a.value) {
                b.value = a.value;
            } else if (b.value) {
                a.value = b.value;
            }
            a.addEventListener('input', function () {
                b.value = a.value;
            });
            b.addEventListener('input', function () {
                a.value = b.value;
            });
        });
        var ageAt = function (dob, ref) {
            var age = ref.getFullYear() - dob.getFullYear();
            var m = ref.getMonth() - dob.getMonth();
            if (m < 0 || (m === 0 && ref.getDate() < dob.getDate())) {
                age--;
            }
            return age;
        };
        var todayIso = new Date().toISOString().slice(0, 10);
        paxSection.querySelectorAll('.pax-row').forEach(function (row) {
            var dobInput = row.querySelector('.pax-dob');
            var idInput = row.querySelector('.pax-id');
            var badge = row.querySelector('.pax-type');
            var star = row.querySelector('.pax-id-req');
            dobInput.max = todayIso;
            var update = function () {
                var child = false;
                if (dobInput.value) {
                    child = ageAt(new Date(dobInput.value + 'T00:00:00'), depDate) < 12;
                    badge.textContent = child ? 'Trẻ em' : 'Người lớn';
                } else {
                    badge.textContent = '';
                }
                idInput.required = !child;
                star.style.display = child ? 'none' : '';
            };
            dobInput.addEventListener('input', update);
            update();
        });
    }

    /* 4c. Provider: thêm / xóa dòng "ngày khởi hành + số chỗ" (my-tours.jsp, provider-tour-detail.jsp).
           Ủy quyền sự kiện để chạy cả với modal. */
    var todayMin = new Date().toISOString().slice(0, 10);
    document.querySelectorAll('[data-dep-row] input[type=date]').forEach(function (d) {
        d.min = todayMin;
    });
    document.addEventListener('click', function (e) {
        var addBtn = e.target.closest ? e.target.closest('[data-dep-add]') : null;
        var delBtn = e.target.closest ? e.target.closest('[data-dep-remove]') : null;
        if (addBtn) {
            var box = addBtn.closest('[data-dep-box]');
            var rows = box.querySelector('[data-dep-rows]');
            var copy = rows.querySelector('[data-dep-row]').cloneNode(true);
            copy.querySelectorAll('input').forEach(function (i) {
                i.value = '';
            });
            rows.appendChild(copy);
        } else if (delBtn) {
            var container = delBtn.closest('[data-dep-rows]');
            if (container.querySelectorAll('[data-dep-row]').length > 1) {
                delBtn.closest('[data-dep-row]').remove();
            }
        }
    });

    /* 4d. Provider: mở hộp thoại nhập lý do hủy (cancel một ngày / cancelAll cả tour) */
    document.addEventListener('click', function (e) {
        var b = e.target.closest ? e.target.closest('[data-cancel-action]') : null;
        if (!b) {
            return;
        }
        var set = function (id, v) {
            var el = document.getElementById(id);
            if (el) {
                if (el.tagName === 'INPUT') {
                    el.value = v;
                } else {
                    el.textContent = v;
                }
            }
        };
        set('cancelAction', b.dataset.cancelAction);
        set('cancelDepId', b.dataset.cancelDep || '');
        set('cancelTitle', b.dataset.cancelTitle || 'Ngừng cung cấp');
    });

    /* 4e. Trang chủ: ô ngày khởi hành không cho chọn quá khứ */
    var searchDate = document.getElementById('searchDate');
    if (searchDate) {
        searchDate.min = new Date().toISOString().slice(0, 10);
    }

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