# Running Behind the Proxy (Crucible / VS Code)

In the training environment the app is **not** opened in the browser at `localhost:3000`
but via a proxy under a subpath:

```text
https://crucible.ch.innoq.io/t/<token>/s/<session>/proxy/3000/
```

The concrete path is available in the env variable `VSCODE_PROXY_URI`
(e.g. `https://crucible.ch.innoq.io/t/.../s/.../proxy/{{port}}/`).

## Symptom

Many **404** errors in the browser for `/_next/static/chunks/*.js` (webpack.js, main-app.js,
page.js …), the page hangs while loading.

## Cause

Next.js generates **absolute** asset paths by default (`/_next/...`). The browser
resolves them against the bare origin (`crucible.ch.innoq.io/_next/...`) — i.e. **without**
the `…/proxy/3000/` prefix. The proxy finds nothing there → 404. The same happens with
absolute `fetch("/api/...")` calls.

## Fix

1. **Assets** – derive `assetPrefix` from `VSCODE_PROXY_URI` in `next.config.mjs`
   (with fallback `undefined` for local):

   ```js
   const proxyUri = process.env.VSCODE_PROXY_URI;
   const assetPrefix = proxyUri
     ? new URL(proxyUri.replace("{{port}}", "3000")).pathname.replace(/\/$/, "")
     : undefined;
   const nextConfig = { assetPrefix };
   ```

   This prepends the proxy path to asset URLs; the proxy strips it again when
   forwarding, so the files are found under `/_next/...` on `localhost:3000`.

2. **API calls** – fetch **relatively** instead of absolutely in the client (no leading slash):

   ```js
   fetch("api/standorte")   // not "/api/standorte"
   ```

   This works locally (`localhost:3000/api/standorte`) and behind the proxy
   (`…/proxy/3000/api/standorte`) alike.

3. After changes to `next.config.mjs`, **restart** the dev server (`npm run dev`).

> Note: `basePath` is **not** suitable here, because the proxy strips the path prefix when
> forwarding — the dev server would then expect the pages under the wrong path and return 404.
