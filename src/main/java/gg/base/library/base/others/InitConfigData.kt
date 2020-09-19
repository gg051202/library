package gg.base.library.base.others

import gg.base.library.base.BaseViewModel

class InitConfigData(var layoutId: Int,
                     var title: String = "标题",
                     var showActionBar: Boolean = true,
                     var needShowBack: Boolean = true,
                     var viewModel: BaseViewModel,
                     var onClickProxy: Any)
