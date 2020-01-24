# aaaah

## Another Android Adapter Abstraction Here

[![](https://jitpack.io/v/chRyNaN/aaaah.svg)](https://jitpack.io/#chRyNaN/aaaah)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-aaaah-green.svg?style=flat)](https://android-arsenal.com/details/1/7535)

## Building

The library is provided through [Bintray](https://bintray.com/). Refer to the [releases](https://github.com/chRyNaN/aaaah/releases) for the latest version.

### Repository
```kotlin
repositories {
    maven {
        url = uri("https://dl.bintray.com/chrynan/chrynan")
    }
}
```

### Dependencies
**Base Library:**
```groovy
implementation 'com.chrynan.aaaah:aaaah-library:VERSION'
```
**Base Androidx Library:**
```groovy
implementation 'com.chrynan.aaaah:aaaah-libraryx:VERSION'
```
**Core Common (Kotlin Multi-platform Classes):**
```groovy
implementation 'com.chrynan.aaaah:aaaah-core:VERSION'
```
**Core JVM (Kotlin Multi-platform Classes):**
```groovy
implementation 'com.chrynan.aaaah:aaaah-core-jvm:VERSION'
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
class MyAdapter: AnotherAdapter<MyItem>() {

  override val viewType = AdapterViewType.from(this::class.java)
  
  override fun onHandlesItem(item: Any) = item is MyItem
  
  override fun onCreateView(parent: ViewGroup, viewType: ViewType): View =
    LayoutInflater.from(parent.context).inflate(R.layout.my_adapter_layout_file, parent, false)
    
  override fun onBindItem(view: View, item: MyItem) {
    // Bind the Item to the View
  }
}
```

* Create the `ManagerRecyclerViewAdapter` which handles the coordination between multiple `AnotherAdapter`s:
```kotlin
val managerAdapter = ManagerRecyclerViewAdapter(adapters = setOf(MyAdapter()))
```

* Add the `ManagerRecyclerViewAdapter` to the `RecyclerView`:
```kotlin
recyclerView?.apply {
  adapter = managerAdapter
  layoutManager = LinearLayoutManager(context) // Or whatever LayoutManager needed
}
```

### Quick Adapter Creation
There is a convenience function which is a shortened syntax to create an Adapter. This could be useful for quick basic adapters.
```kotlin
val myAdapter = anotherAdapter<ItemType>(viewType = myViewType, viewResId = R.layout.my_adapter_layout) { item ->
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

If only one Adapter is needed, there's no need to explicitly wrap the Adapter, just call the `RecyclerView.adapter()` extension function:
```kotlin
recyclerView?.apply {
    adapter(myAdapter)
    layoutManager = LinearLayoutManager(context) // Or whatever LayoutManager needed
}
```

## Annotation Processor

By default, the `AdapterViewType.from()` function returns the Hash Code of the `Class`. For most use cases this should be sufficient. However, if guaranteed unique View Types are needed for each Adapter, the Annotation Processor could be used.

* Annotate the Adapter with the `Adapter` annotation (available in the Annotations library):
```kotlin
@Adapter
class MyAdapter: AnotherAdapter<MyItem>() {

  override val viewType = AdapterViewType.from(this::class.java)
  
  override fun onHandlesItem(item: Any) = item is MyItem
  
  override fun onCreateView(parent: ViewGroup, viewType: ViewType): View =
    LayoutInflater.from(parent.context).inflate(R.layout.my_adapter_layout_file, parent, false)
    
  override fun onBindItem(view: View, item: MyItem) {
    // Bind the Item to the View
  }
}
```

* Build the project (make sure to have both the compiler and annotations libraries in the dependencies)

A class, `AdapterViewTypes`, will be generated with View Type constants for each class annotated with the `Adapter` annotation. Also, a more specific `AdapterViewType.from()` extension function will be generated. Either approach could be used to access the View Type for each adapter:
```kotlin
AdapterViewTypes.MY_ADAPTER
// or
AdapterViewType.from(MyAdapter::class.java)
```

### Naming the generated constants

The generated constant names can be overriden by providing a value to the `name` parameter in the `Adapter` annotation:
```kotlin
@Adapter(name = "MyConstantName")
// Results in:
AdapterViewTypes.MyConstantName
```

## DSL

```kotlin
myRecyclerView.adapter = anotherAdapterManager<UniqueAdapterItem> { // Or could use `adapters { ... }`
    // Use the DSL directly
    anotherAdapter<ItemTypeOne> {
        viewType = AdapterViewTypes.ITEM_ONE
        handlesItem { it is ItemTypeOne }
        createView { parent, viewType -> 
            LayoutInflater.from(parent.context).inflate(R.layout.my_adapter_layout_file, parent, false)
        }
        bindItem { view, item -> 
            // Bind the Item to the View
        }
    }

    // Or Provide an already created adapter
    anotherAdapter<ItemTypeTwo>(myAdapter)
}
```

## Processing Item Changes
The library comes with a `DiffUtilCalculator` class which is a basic implementation of RecyclerView's `DiffUtil.Callback` for a `UniqueAdapterItem`. This class can be used for most cases but if additional functionality is needed, the class is extensible. The `DiffUtilCalculator.calculateDiff()` function takes in a parameter of list items and calculates the diff and returns an `AndroidDiffResult` which is a wrapper around the RecyclerView's `DiffUtil.DiffResult`.
```kotlin
val diffCalculator = DiffUtilCalculator<UniqueAdapterItem>()

val result = diffCalculator.calculateDiff(sortedItems = myNewListItems)

// myListUpdater is an implementation of the ItemListUpdater interface
myListUpdater.items = result.items
result.diffUtilResult.dispatchItemsTo(myListUpdater)
```

The library contains a `DiffProcessor` interface in the Kotlin common `core` module and an `AndroidDiffProcessor` implementation which encapsulates the processing logic.
```kotlin
val processor = AndroidDiffProcessor(DiffUtilCalculator())

val result = processor.processDiff(myNewListItems)
```

The library also contains a `DiffDispatcher` interface in the Kotlin common `core` module and an `AndroidDiffDispatcher` implementation which encapsulates the dispatching logic.
```kotlin
val dispatcher = AndroidDiffDispatcher(myItemListUpdater)

dispatcher.dispatchDiff(result)
```

Both the `DiffProcessor.processDiff` and the `DiffDispatcher.dispatchDiff` functions are *suspending functions*. This is because these tasks should be performed off the UI Thread.

One common use case is to process and dispatch diffs from a Kotlin Coroutine `Flow`. Since this library doesn't have the Coroutine Library as a dependency, there is some manual work involved to simplify this approach. A recommend approach is the following:
```kotlin
interface AdapterItemHandler<VM : UniqueAdapterItem> {

    fun Flow<Collection<VM>>.calculateAndDispatchDiff(): Flow<DiffResult<VM>>
}

class BaseAdapterItemHandler<VM : UniqueAdapterItem> @Inject constructor(
        private val diffProcessor: DiffProcessor<VM>,
        private val diffDispatcher: DiffDispatcher<VM>,
        private val coroutineDispatchers: CoroutineDispatchers // Note this is just a convenience wrapper around the available Coroutine Dispatchers
) : AdapterItemHandler<VM> {

    @ExperimentalCoroutinesApi
    override fun Flow<Collection<VM>>.calculateAndDispatchDiff(): Flow<DiffResult<VM>> =
            map(diffProcessor::processDiff)
                    .flowOn(coroutineDispatchers.io)
                    .onEach { diffDispatcher.dispatchDiff(it) }
                    .flowOn(coroutineDispatchers.main)
}

class MyPresenter @Inject constructor(
    adapterItemHandler: AdapterItemHandler<UniqueAdapterItem>,
    private val scope: CoroutineScope,
    private val mapper: MyMapper,
    private val repo: MyRepository
) : Presenter,
    AdapterItemHandler<UniqueAdapterItem> by adapterItemHandler {

    fun getItems(){
        repo.getItems()
            .map(mapper::suspendMapping)
            .calculateAndDispatchDiff()
            .launchIn(scope)
    }
}
``` 