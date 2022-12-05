# RecyclerView accessibility bug demo project

It is a demo project to demonstrate a bug with integration between `RecyclerView` and accessibility.

## Bug

When `RecyclerView` prefetches future `ViewHolder` via `GapWorker` sometimes it does not invoke bind
method. When such `ViewHolder` is recycled, `ViewCompat.setAccessibilityDelegate` is invoked
passing `null` as a parameter, leading to losing original accessibility delegate, that was set
on `ViewHolder` view.

It does not happen with a regular (non-prefetch) flow because `bind` method is always invoked, and
the original delegate is stored.

## Steps

1. Start the app.
2. Scroll.
3. See a crash at `CustomView.setAccessibilityDelegate` or `Adapter.onBindViewHolder` because the
   view delegate is neither `RecyclerView` accessibility delegate nor the original custom one. It
   was lost during prefetch by `GapWorker`.

## License

```
Copyright 2022 Bumble

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
