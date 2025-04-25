(() => {
    "use strict";
    var r = {
        319: (r, e, t) => {
            var n = t(751), o = t(641);
            const i = {id: "app"};

            function a(r, e, t, n, a, u) {
                const f = (0, o.g2)("router-link"), l = (0, o.g2)("router-view");
                return (0, o.uX)(), (0, o.CE)("div", i, [(0, o.Lk)("nav", null, [(0, o.bF)(f, {to: "/"}, {
                    default: (0, o.k6)((() => e[0] || (e[0] = [(0, o.eW)("活动管理")]))),
                    _: 1
                }), (0, o.bF)(f, {to: "/prizes"}, {
                    default: (0, o.k6)((() => e[1] || (e[1] = [(0, o.eW)("奖品管理")]))),
                    _: 1
                }), (0, o.bF)(f, {to: "/cache"}, {
                    default: (0, o.k6)((() => e[2] || (e[2] = [(0, o.eW)("缓存管理")]))),
                    _: 1
                })]), (0, o.bF)(l)])
            }

            const u = {name: "App"};
            var f = t(262);
            const l = (0, f.A)(u, [["render", a]]), c = l;
            (0, n.Ef)(c).mount("#app")
        }
    }, e = {};

    function t(n) {
        var o = e[n];
        if (void 0 !== o) return o.exports;
        var i = e[n] = {exports: {}};
        return r[n](i, i.exports, t), i.exports
    }

    t.m = r, (() => {
        var r = [];
        t.O = (e, n, o, i) => {
            if (!n) {
                var a = 1 / 0;
                for (c = 0; c < r.length; c++) {
                    for (var [n, o, i] = r[c], u = !0, f = 0; f < n.length; f++) (!1 & i || a >= i) && Object.keys(t.O).every((r => t.O[r](n[f]))) ? n.splice(f--, 1) : (u = !1, i < a && (a = i));
                    if (u) {
                        r.splice(c--, 1);
                        var l = o();
                        void 0 !== l && (e = l)
                    }
                }
                return e
            }
            i = i || 0;
            for (var c = r.length; c > 0 && r[c - 1][2] > i; c--) r[c] = r[c - 1];
            r[c] = [n, o, i]
        }
    })(), (() => {
        t.d = (r, e) => {
            for (var n in e) t.o(e, n) && !t.o(r, n) && Object.defineProperty(r, n, {enumerable: !0, get: e[n]})
        }
    })(), (() => {
        t.g = function () {
            if ("object" === typeof globalThis) return globalThis;
            try {
                return this || new Function("return this")()
            } catch (r) {
                if ("object" === typeof window) return window
            }
        }()
    })(), (() => {
        t.o = (r, e) => Object.prototype.hasOwnProperty.call(r, e)
    })(), (() => {
        var r = {524: 0};
        t.O.j = e => 0 === r[e];
        var e = (e, n) => {
            var o, i, [a, u, f] = n, l = 0;
            if (a.some((e => 0 !== r[e]))) {
                for (o in u) t.o(u, o) && (t.m[o] = u[o]);
                if (f) var c = f(t)
            }
            for (e && e(n); l < a.length; l++) i = a[l], t.o(r, i) && r[i] && r[i][0](), r[i] = 0;
            return t.O(c)
        }, n = self["webpackChunklottery_frontend"] = self["webpackChunklottery_frontend"] || [];
        n.forEach(e.bind(null, 0)), n.push = e.bind(null, n.push.bind(n))
    })();
    var n = t.O(void 0, [504], (() => t(319)));
    n = t.O(n)
})();
//# sourceMappingURL=app.34a1c45f.js.map