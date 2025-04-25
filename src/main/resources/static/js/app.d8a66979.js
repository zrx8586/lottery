(() => {
    "use strict";
    var e = {
        681: (e, t, r) => {
            var n = r(751), l = r(641);

            function o(e, t, r, n, o, i) {
                const a = (0, l.g2)("prize-management");
                return (0, l.uX)(), (0, l.CE)("div", null, [t[0] || (t[0] = (0, l.Lk)("h1", null, "Lottery Management", -1)), (0, l.bF)(a)])
            }

            var i = r(33);

            function a(e, t, r, n, o, a) {
                return (0, l.uX)(), (0, l.CE)("div", null, [t[1] || (t[1] = (0, l.Lk)("h2", null, "奖品列表", -1)), (0, l.Lk)("table", null, [t[0] || (t[0] = (0, l.Lk)("thead", null, [(0, l.Lk)("tr", null, [(0, l.Lk)("th", null, "奖品ID"), (0, l.Lk)("th", null, "奖品名称"), (0, l.Lk)("th", null, "库存"), (0, l.Lk)("th", null, "概率")])], -1)), (0, l.Lk)("tbody", null, [((0, l.uX)(!0), (0, l.CE)(l.FK, null, (0, l.pI)(o.prizes, (e => ((0, l.uX)(), (0, l.CE)("tr", {key: e.activityPrizeId}, [(0, l.Lk)("td", null, (0, i.v_)(e.activityPrizeId), 1), (0, l.Lk)("td", null, (0, i.v_)(e.prizeName), 1), (0, l.Lk)("td", null, (0, i.v_)(e.quantity), 1), (0, l.Lk)("td", null, (0, i.v_)(e.probability), 1)])))), 128))])])])
            }

            var u = r(335);
            const d = u.A.create({baseURL: "/api", headers: {"Content-Type": "application/json"}}),
                p = e => d.get(`/activity-prizes/${e}`), s = {
                    data() {
                        return {prizes: []}
                    }, async mounted() {
                        const e = await p(1);
                        this.prizes = e.data
                    }
                };
            var v = r(262);
            const c = (0, v.A)(s, [["render", a]]), f = c, y = {components: {PrizeManagement: f}},
                b = (0, v.A)(y, [["render", o]]), h = b;
            (0, n.Ef)(h).mount("#app")
        }
    }, t = {};

    function r(n) {
        var l = t[n];
        if (void 0 !== l) return l.exports;
        var o = t[n] = {exports: {}};
        return e[n](o, o.exports, r), o.exports
    }

    r.m = e, (() => {
        var e = [];
        r.O = (t, n, l, o) => {
            if (!n) {
                var i = 1 / 0;
                for (p = 0; p < e.length; p++) {
                    for (var [n, l, o] = e[p], a = !0, u = 0; u < n.length; u++) (!1 & o || i >= o) && Object.keys(r.O).every((e => r.O[e](n[u]))) ? n.splice(u--, 1) : (a = !1, o < i && (i = o));
                    if (a) {
                        e.splice(p--, 1);
                        var d = l();
                        void 0 !== d && (t = d)
                    }
                }
                return t
            }
            o = o || 0;
            for (var p = e.length; p > 0 && e[p - 1][2] > o; p--) e[p] = e[p - 1];
            e[p] = [n, l, o]
        }
    })(), (() => {
        r.d = (e, t) => {
            for (var n in t) r.o(t, n) && !r.o(e, n) && Object.defineProperty(e, n, {enumerable: !0, get: t[n]})
        }
    })(), (() => {
        r.g = function () {
            if ("object" === typeof globalThis) return globalThis;
            try {
                return this || new Function("return this")()
            } catch (e) {
                if ("object" === typeof window) return window
            }
        }()
    })(), (() => {
        r.o = (e, t) => Object.prototype.hasOwnProperty.call(e, t)
    })(), (() => {
        r.r = e => {
            "undefined" !== typeof Symbol && Symbol.toStringTag && Object.defineProperty(e, Symbol.toStringTag, {value: "Module"}), Object.defineProperty(e, "__esModule", {value: !0})
        }
    })(), (() => {
        var e = {524: 0};
        r.O.j = t => 0 === e[t];
        var t = (t, n) => {
            var l, o, [i, a, u] = n, d = 0;
            if (i.some((t => 0 !== e[t]))) {
                for (l in a) r.o(a, l) && (r.m[l] = a[l]);
                if (u) var p = u(r)
            }
            for (t && t(n); d < i.length; d++) o = i[d], r.o(e, o) && e[o] && e[o][0](), e[o] = 0;
            return r.O(p)
        }, n = self["webpackChunklottery_frontend"] = self["webpackChunklottery_frontend"] || [];
        n.forEach(t.bind(null, 0)), n.push = t.bind(null, n.push.bind(n))
    })();
    var n = r.O(void 0, [504], (() => r(681)));
    n = r.O(n)
})();
//# sourceMappingURL=app.d8a66979.js.map