/*
 * Tự động cập nhật dữ liệu "gần như thời gian thực" cho các trang danh sách.
 *
 * Cách dùng: gắn thuộc tính data-live vào khối HTML cần luôn mới (bảng danh sách, ô thống kê...).
 *   <div class="panel" data-live="users"> ... </div>
 * Cứ mỗi vài giây trang gửi lại chính URL hiện tại, so sánh phần data-live với lần trước và chỉ thay
 * đúng phần đã đổi. Không cần tải lại trang, không mất vị trí cuộn.
 *
 * Tạm dừng (không ghi đè) khi: tab đang ẩn, đang mở hộp thoại (modal), hoặc người dùng đang gõ trong
 * ô nhập liệu / chọn trong danh sách thả, để không làm mất những gì đang thao tác.
 * Nếu phiên đăng nhập đã bị kết thúc (ví dụ tài khoản vừa bị khóa) thì tự chuyển về trang đăng nhập.
 */
(function () {
    'use strict';

    var INTERVAL_MS = 5000;

    var regions = document.querySelectorAll('[data-live]');
    if (!regions.length || !window.fetch || !window.DOMParser) {
        return;
    }

    // HTML do server trả về lần gần nhất cho từng vùng (so sánh với bản này, không so với DOM đã bị script thay đổi)
    var lastHtml = [];
    var inFlight = false;
    var badge = null;
    var badgeTimer = null;

    for (var i = 0; i < regions.length; i++) {
        lastHtml[i] = regions[i].innerHTML;
    }

    function createBadge() {
        badge = document.createElement('div');
        badge.setAttribute('aria-live', 'polite');
        badge.style.cssText = 'position:fixed;right:16px;bottom:16px;z-index:2000;padding:6px 12px;'
                + 'border-radius:999px;font:600 12px Inter,Arial,sans-serif;color:#066b54;background:#e6f4ef;'
                + 'border:1px solid #b7dfd2;box-shadow:0 2px 8px rgba(0,0,0,.08);pointer-events:none';
        setBadge('\u25CF Trực tiếp', false);
        document.body.appendChild(badge);
    }

    function setBadge(text, highlight) {
        if (!badge) {
            return;
        }
        badge.textContent = text;
        badge.style.background = highlight ? '#066b54' : '#e6f4ef';
        badge.style.color = highlight ? '#ffffff' : '#066b54';
    }

    function showUpdated() {
        setBadge('\u2714 Đã cập nhật', true);
        if (badgeTimer) {
            clearTimeout(badgeTimer);
        }
        badgeTimer = setTimeout(function () {
            setBadge('\u25CF Trực tiếp', false);
        }, 2000);
    }

    /** Có đang thao tác khiến việc thay nội dung gây phiền không? */
    function userIsBusy() {
        if (document.hidden) {
            return true;
        }
        if (document.querySelector('.modal.show')) {
            return true;
        }
        var active = document.activeElement;
        if (active && /^(INPUT|TEXTAREA|SELECT)$/.test(active.tagName) && active.closest('[data-live]')) {
            return true;
        }
        return false;
    }

    function applyUpdate(html) {
        var doc = new DOMParser().parseFromString(html, 'text/html');
        var fresh = doc.querySelectorAll('[data-live]');
        if (fresh.length !== regions.length) {
            return;                                   // trang trả về không phải trang này (ví dụ trang lỗi)
        }
        var changed = false;
        for (var i = 0; i < regions.length; i++) {
            var newHtml = fresh[i].innerHTML;
            if (newHtml !== lastHtml[i]) {
                if (userIsBusy()) {
                    return;                           // thử lại ở lần sau, chưa ghi nhận là đã cập nhật
                }
                regions[i].innerHTML = newHtml;
                lastHtml[i] = newHtml;
                changed = true;
            }
        }
        if (changed) {
            showUpdated();
            document.dispatchEvent(new CustomEvent('live-refresh:updated'));
        }
    }

    function poll() {
        if (inFlight || userIsBusy()) {
            return;
        }
        inFlight = true;
        fetch(window.location.href, {
            credentials: 'same-origin',
            cache: 'no-store',
            headers: {'X-Requested-With': 'live-refresh'}
        }).then(function (res) {
            // Bị chuyển về trang đăng nhập nghĩa là phiên đã hết hoặc tài khoản đã bị khóa
            if (res.redirected && /\/login(\?|#|$)/.test(res.url)) {
                window.location.href = res.url;
                return null;
            }
            return res.ok ? res.text() : null;
        }).then(function (html) {
            if (html) {
                applyUpdate(html);
            }
        }).catch(function () {
            /* mất mạng tạm thời: bỏ qua, lần sau thử lại */
        }).then(function () {
            inFlight = false;
        });
    }

    createBadge();
    setInterval(poll, INTERVAL_MS);
    document.addEventListener('visibilitychange', function () {
        if (!document.hidden) {
            poll();
        }
    });
})();
