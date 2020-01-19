# aaaah

## Another Android Adapter Abstraction Here

[![](https://jitpack.io/v/chRyNaN/aaaah.svg)](https://jitpack.io/#chRyNaN/aaaah)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-aaaah-green.svg?style=flat)](https://android-arsenal.com/details/1/7535)

## Building

The library is provided through [Github Packages](https://github.com/chRyNaN/aaaah/packages). Refer to the [releases](https://github.com/chRyNaN/aaaah/releases) for the latest version.

### Repository
```kotlin
maven {
    url = uri("https://maven.pkg.github.com/chRyNaN/aaaah")
    credentials {
        // "gpr.user" is your Github username stored in your LOCAL git properties file
        // "gpr.key" is your Github personal access token stored in your LOCAL git properties file
        // Note that these may not be required since the project is public
        username = project.findProperty("gpr.user") as? String?
        password = project.findProperty("gpr.key") as? String?
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
  layoutManager = LinearLayoutManager(context) // Or Whatever LayoutManager Needed
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
    anotherAdapter<ItemOne> {
        viewType = AdapterViewTypes.ITEM_ONE
        handlesItem { it is ItemOne }
        createView { parent, viewType -> 
            LayoutInflater.from(parent.context).inflate(R.layout.my_adapter_layout_file, parent, false)
        }
        bindItem { view, item -> 
            // Bind the Item to the View
        }
    }
}
```
