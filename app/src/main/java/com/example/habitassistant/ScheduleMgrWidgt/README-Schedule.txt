日程管理模块
->实现周视图、日视图组件搭建。周视图、日视图组件实现基于不同视图实现活动的展现，提供了多个自定义监    听器，如点击事件、滑动事件等和自定义组件样式。同时，由于好的封装设计，可以轻松切换周视图和日视图
->基于自建组件搭建周视图、日视图以及搭建日程视图界面。日程视图以标签的形式展现活动顺序，可通过点击    对应日期 ，滑动到对应日期标签处。也可点击按钮直接回到当天活动处。
->使用Okhttp异步请求获取对应数据，并在视图上进行渲染。


下面是对ScheduleMgrWidgt包下的各包的介绍：
adapter包:适配器包，内含适配器类。负责为viewpager准备页面数据。主要是为了实现滑动切换功能。

library包：内含自建周视图、日视图组件相关代码，通过library进行封装保存。

Listener包：内含监听器，负责监听用户行为并进行回调。



下面是对ScheduleMgrWidgt包下的各类的介绍：
>>adapter包下：

ScheduleAdapter：负责准备和装填周视图、日视图以及日程视图界面。

>>library包下：

CalendarView:自定义组件，继承于LinearLayout。其主体依旧是一个ViewPager,这样可以实现手指滑动切换页面。该类包含许多成员变量，包括样式变量、适配器变量、内部适配器类以及自定义监听器。是组件重点类。

ScheduleView：主要体现为手指滑动时替换的页面。该类包含向外提供的样式接口，以及内部样式接口、监听器、各颜色常量。其主要通过Android原生画图机制进行界面的绘制。

CalendarViewEvent：事件。该类目的主要是封装日视图和周视图需要的数据。CalendarView通过设置该事件进行图片的渲染。

ScheduleViewEventDrawBO：该类的目的主要是封装ScheduleView画图时所需要的位置、长宽信息。在CalendarView设置CalendarViewEvent后，ScheduleView对象需要处理CalendarViewEvent的集合来获取ScheduleViewEventDrawBO的集合对象，进而渲染。

OnEventClickListener：自定义点击事件监听器，提供一个向外的接口，帮助使用该组件的人实现自定义事件。比如：点击后创建日程。

OnEventLongPressListener：自定义长按事件监听器，提供一个向外的接口，帮助使用该组件的人实现自定义事件。

OnSchedulerPageChangedListener：自定义页面切换事件监听器，提供一个向外的接口，帮助使用该组件的人实现自定义事件。

>> Listener包下：

NavTextViewListener：用于监听器测试。


>>ScheduleMgrWidgt包下：

ScheduleAgendaFragment：日程视图。继承于Fragment，为了实现滑动切换页面。该类目的主要是为了以标签的形式展现活动顺序，可通过点击    对应日期 ，滑动到对应日期标签处。也可点击按钮直接回到当天活动处

ScheduleDayFragment：日视图。继承于Fragment，为了实现滑动切换页面。该类目的主要是为了基于自建的日视图组件搭建一个完整的页面。

ScheduleWeekFragment：周视图。继承于Fragment，为了实现滑动切换页面。该类目的主要是为了基于自建的周视图组件搭建一个完整的页面。

ScheduleMonthFragment：月视图。继承于Fragment，为了实现滑动切换页面。该类目的主要是为了基于现有日历组件搭建一个完整的页面。

DrawableCalendarEvent：事件。该类目的主要是封装日程视图需要的数据。日程视图通过它可以解析并渲染出数据。特点是可在标签上带有图标。





下面是对utils包下的部分类的介绍：
OkhttpHelper:负责使用get方式和服务器异步通信。
Unitutils:负责单位的转换
