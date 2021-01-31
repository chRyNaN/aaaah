# aaaah

## Another Android Adapter Abstraction Here

A lightweight and easy to use abstraction for Android RecyclerViews. <br>
<img alt="GitHub tag (latest by date)" src="https://img.shields.io/github/v/tag/chRyNaN/aaaah">

```kotlin
@Adapter
class EmojiListItemAdapter @Inject constructor() : AnotherAdapter<EmojiListItemViewModel>() {

    override val viewType: ViewType = AdapterViewType.from(EmojiListItemAdapter::class.java)

    override fun onHandlesItem(item: Any): Boolean = item is EmojiListItemViewModel

    override fun onCreateView(parent: ViewGroup, inflater: LayoutInflater, viewType: ViewType): View =
        inflater.inflate(R.layout.adapter_emoji_list_item, parent, false)

    override fun View.onBindItem(item: EmojiListItemViewModel, position: Int) {
        adapterEmojiWidget?.emojiViewModel = item.viewModel
    }
}
```

## Building

The library is provided through [Bintray](https://bintray.com/). Refer to
the [releases](https://github.com/chRyNaN/aaaah/releases) for the latest version.

### Repository

```kotlin
repositories {
    maven {
        url = uri("https://dl.bintray.com/chrynan/chrynan")
    }
}
```

### Dependencies

**Android Library:**

```groovy
implementation 'com.chrynan.aaaah:aaaah-libraryx:VERSION'
```

**Core Common (Kotlin Multi-platform Classes):**

```groovy
implementation 'com.chrynan.aaaah:aaaah-core:VERSION'
```

**Annotations (Optional):**

```groovy
implementation 'com.chrynan.aaaah:aaaah-annotation:VERSION'
```

**Annotation Processor (Optional):**

```groovy
kapt 'com.chrynan.aaaah:aaaah-compiler:VERSION'
```

**DSL (Optional):**

```groovy
implementation 'com.chrynan.aaaah:aaaah-dsl:VERSION'
```

## Using the Library

* Create an `AnotherAdapter` implementation:

```kotlin
class MyAdapter : AnotherAdapter<MyItem>() {

    override val viewType = AdapterViewType.from(this::class.java)

    override fun onHandlesItem(item: Any) = item is MyItem

    override fun onCreateView(parent: ViewGroup, inflater: LayoutInflater, viewType: ViewType): View =
        inflater.inflate(R.layout.my_adapter_layout_file, parent, false)

    override fun View.onBindItem(item: MyItem, position: Int) {
        // Bind the Item to the View
    }
}
```

* Create the `ManagerRecyclerViewAdapter` which handles the coordination between multiple `AnotherAdapter`s:

```kotlin
val managerAdapter = anotherAdapterManager(MyAdapter()) // vararg parameters
```

* Add the `ManagerRecyclerViewAdapter` to the `RecyclerView`:

```kotlin
recyclerView?.apply {
    adapter = managerAdapter
    layoutManager = LinearLayoutManager(context) // Or whatever LayoutManager needed
}
```

### Quick Adapter Creation

There is a convenience function which is a shortened syntax to create an Adapter. This could be useful for quick basic
adapters.

```kotlin
val myAdapter =
    anotherAdapter<ItemType>(viewType = myViewType, viewResId = R.layout.my_adapter_layout) { item, position ->
        // this refers to the containing Android View
        this.findViewById<TextView>(R.id.myTextView)?.text = item.title
    }
```

Then to assign the Adapter to a RecyclerView, wrap it in a `ManagerRecyclerViewAdapter`:

```kotlin
recyclerView?.apply {
    adapter = anotherManagerAdapter(myAdapter)
    layoutManager = LinearLayoutManager(context) // Or whatever LayoutManager needed
}
```

If only one Adapter is needed, there's no need to explicitly wrap the Adapter, just call the `RecyclerView.adapter()`
extension function:

```kotlin
recyclerView?.apply {
    adapter(myAdapter)
    layoutManager = LinearLayoutManager(context) // Or whatever LayoutManager needed
}
```

## Annotation Processor

By default, the `AdapterViewType.from()` function returns the Hash Code of the `Class`. For most use cases this should
be sufficient. However, if guaranteed unique View Types are needed for each Adapter, the Annotation Processor could be
used.

* Annotate the Adapter with the `Adapter` annotation (available in the Annotations library):

```kotlin
@Adapter
class MyAdapter : AnotherAdapter<MyItem>() {

    override val viewType = AdapterViewType.from(this::class.java)

    override fun onHandlesItem(item: Any) = item is MyItem

    override fun onCreateView(parent: ViewGroup, inflater: LayoutInflater, viewType: ViewType): View =
        inflater.inflate(R.layout.my_adapter_layout_file, parent, false)

    override fun View.onBindItem(item: MyItem, position: Int) {
        // Bind the Item to the View
    }
}
```

* Build the project (make sure to have both the compiler and annotations libraries in the dependencies)

A class, `AdapterViewTypes`, will be generated with View Type constants for each class annotated with the `Adapter`
annotation. Also, a more specific `AdapterViewType.from()` extension function will be generated. Either approach could
be used to access the View Type for each adapter:

```kotlin
AdapterViewTypes.MY_ADAPTER
// or
AdapterViewType.from(MyAdapter::class.java)
```

### Naming the generated constants

The generated constant names can be overridden by providing a value to the `name` parameter in the `Adapter` annotation:

```kotlin
@Adapter(name = "MyConstantName")
// Results in:
AdapterViewTypes.MyConstantName
```

## Processing Item Changes

The library comes with a `DiffUtilCalculator` class which is a basic implementation of
RecyclerView's `DiffUtil.Callback` for a `UniqueAdapterItem`. This class can be used for most cases but if additional
functionality is needed, the class is extensible. The `DiffUtilCalculator.calculateDiff()` function takes in a parameter
of list items and calculates the diff and returns an `AndroidDiffResult` which is a wrapper around the
RecyclerView's `DiffUtil.DiffResult`.

```kotlin
val diffCalculator = DiffUtilCalculator<UniqueAdapterItem>()

val result = diffCalculator.calculateDiff(sortedItems = myNewListItems)

// myListUpdater is an implementation of the ItemListUpdater interface
myListUpdater.items = result.items
result.diffUtilResult.dispatchItemsTo(myListUpdater)
```

The library contains a `DiffProcessor` interface in the Kotlin common `core` module and an `AndroidDiffProcessor`
implementation which encapsulates the processing logic.

```kotlin
val processor = AndroidDiffProcessor(DiffUtilCalculator())

val result = processor.processDiff(myNewListItems)
```

The library also contains a `DiffDispatcher` interface in the Kotlin common `core` module and an `AndroidDiffDispatcher`
implementation which encapsulates the dispatching logic.

```kotlin
val dispatcher = AndroidDiffDispatcher(myItemListUpdater)

dispatcher.dispatchDiff(result)
```

Both the `DiffProcessor.processDiff` and the `DiffDispatcher.dispatchDiff` functions are *suspending functions*. This is
because these tasks should be performed off the UI Thread.

Using the provided `AdapterItemHandler` interface, processing items is much easier on a Kotlin Coroutine `Flow` of
items:

```kotlin
val adapterItemHandler = BaseAdapterItemHandler(myDiffProcessor, myDiffDispatcher)

flowOf(myItems)
    .calculateAndDispatcherDiff(adapterItemHandler)
    .launchIn(this)
```

## Adapter Factories

Sometimes dynamic Adapter creation is necessary, such as when using nested adapters. For this scenario, there is
the `AdapterFactory` interface along with the `BaseAdapterFactory` abstract class implementation. Creating a
custom `AdapterFactory` implementation is fairly straightforward:

```kotlin
class MyItemAdapterFactory @Inject constructor(
    myItemAdapter: MyItemAdapter,
    context: Context
) : BaseAdapterFactory<ListItemViewModel>() {

    override val adapters: Set<AnotherAdapter<*>> = setOf(myItemAdapter)

    override val positionManager: AdapterPositionManager = VerticalPositionManager(context)
}
```

Then the `AdapterFactory` has to be bound to the `RecyclerView`, and finally it can be used instead of
the `AdapterItemHandler` to calculate and dispatch the diffs:

```kotlin
recyclerView?.bindAdapterFactory(myAdapterFactory)

flowOf(myItems)
    .calculateAndDispatcherDiff(myAdapterFactory)
    .launchIn(this)
```
