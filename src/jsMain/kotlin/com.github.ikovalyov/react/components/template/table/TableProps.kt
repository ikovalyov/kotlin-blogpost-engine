package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.extenstion.extraAttrs
import com.github.ikovalyov.model.markers.BodyInterface
import com.github.ikovalyov.model.markers.IdInterface
import com.github.ikovalyov.styles.Colors
import kotlin.reflect.KClass
import kotlinext.js.jsObject
import kotlinx.css.BorderCollapse
import kotlinx.css.BorderStyle
import kotlinx.css.Cursor
import kotlinx.css.FontWeight
import kotlinx.css.LinearDimension
import kotlinx.css.TextAlign
import kotlinx.css.WhiteSpace
import kotlinx.css.backgroundColor
import kotlinx.css.borderBottom
import kotlinx.css.borderBottomColor
import kotlinx.css.borderCollapse
import kotlinx.css.borderColor
import kotlinx.css.borderRight
import kotlinx.css.borderRightColor
import kotlinx.css.borderSpacing
import kotlinx.css.borderStyle
import kotlinx.css.borderWidth
import kotlinx.css.color
import kotlinx.css.cursor
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.css.textAlign
import kotlinx.css.whiteSpace
import kotlinx.css.width
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.buildElement
import react.dom.div
import react.dom.tr
import react.fc
import react.table.Column
import react.table.RenderType
import react.table.TableInstance
import react.table.columns
import react.table.useTable
import react.useMemo
import styled.css
import styled.styledDiv
import styled.styledTable
import styled.styledTbody
import styled.styledTd
import styled.styledTh
import styled.styledThead
import styled.styledTr

external interface TableProps<T: Any>: PropsWithChildren {
  var items: Array<T>?
  var onEditClick: (T) -> Unit
  var onDeleteClick: (T) -> Unit
  var onViewClick: (T) -> Unit
}

fun <T: Any>buildTableColumns(componentProps: TableProps<T>, forClass: KClass<T>): Array<out Column<Any, *>> {
  return useMemo {
    columns {
//      if (IdInterface::class.isInstance(forClass)) {
        column<String> {
          header = "Id"
          accessorFunction = { (it as? IdInterface)?.id.toString() }
        }
//      }
//      if (forClass.isInstance(BodyInterface::class)) {
        column<String> {
          header = "Body"
          accessorFunction = { (it as? BodyInterface<*>)?.body.toString() }
        }
//      }
//      if (forClass.isInstance(IdInterface::class)) {
        column<String> {
          header = "Action"
          accessor = "id"
          cellFunction = { props ->
            buildElement {
              div {
                child(Button::class) {
                  attrs {
                    onClick = componentProps.onViewClick as (IdInterface) -> Unit
                    body = props.row.original as IdInterface
                    text = "view"
                  }
                }
                child(Button::class) {
                  attrs {
                    onClick = componentProps.onEditClick as (IdInterface) -> Unit
                    body = props.row.original as IdInterface
                    text = "update"
                  }
                }
                child(Button::class) {
                  attrs {
                    onClick = componentProps.onDeleteClick as (IdInterface) -> Unit
                    body = props.row.original as IdInterface
                    text = "delete"
                  }
                }
              }
            }
          }
        }
      }
    }
//  }
}

inline fun <reified T: Any>createTableComponent(): FC<TableProps<T>> {
  return fc { componentProps ->
    val tableColumns = buildTableColumns<T>(componentProps, T::class)
    val table = useTable<Any>(
      options = jsObject {
        this.data = useMemo{ componentProps.items ?: emptyArray<T>() }
        this.columns = tableColumns
      }
    )
    buildTableBody(table)
  }
}

fun RBuilder.buildTableBody(
  table: TableInstance<Any>
) {
  styledDiv {
    styledTable {
      extraAttrs = table.getTableProps()

      css {
        width = 400.px
        borderSpacing = 0.px
        borderCollapse = BorderCollapse.collapse
        whiteSpace = WhiteSpace.nowrap
        borderWidth = 2.px
        borderStyle = BorderStyle.solid
        borderColor = Colors.Stroke.Gray
        margin(LinearDimension.auto)
      }
      styledThead {
        css {
          color = Colors.Text.Gray
          fontSize = 18.px
          backgroundColor = Colors.Background.Gray
        }
        for (headerGroup in table.headerGroups) {
          tr {
            extraAttrs = headerGroup.getHeaderGroupProps()
            for (h in headerGroup.headers) {
              val originalHeader = h.placeholderOf
              val header = originalHeader ?: h

              styledTh {
                extraAttrs = header.getHeaderProps()
                css {
                  fontWeight = FontWeight.normal
                  padding(4.px, 12.px)
                  borderRightColor = Colors.Stroke.Gray
                  borderRight = BorderStyle.solid.toString()
                  if (header.columns != null) {
                    borderBottomColor = Colors.Stroke.Gray
                    borderBottom = BorderStyle.solid.toString()
                  }
                  lastChild { borderRight = "none" }
                }
                +header.render(RenderType.Header)
              }
            }
          }
        }
      }
      styledTbody {
        extraAttrs = table.getTableBodyProps()

        css {
          color = Colors.Text.Black
          backgroundColor = Colors.Background.White
          textAlign = TextAlign.start
        }
        for (row in table.rows) {
          table.prepareRow(row)

          styledTr {
            extraAttrs = row.getRowProps()
            css {
              fontSize = 16.px
              cursor = Cursor.pointer
              borderBottomColor = Colors.Stroke.LightGray
              borderBottom = BorderStyle.solid.toString()
              hover { backgroundColor = Colors.Background.Gray }
            }
            for (cell in row.cells) {
              styledTd {
                extraAttrs = cell.getCellProps()
                css { padding(10.px, 12.px) }
                +cell.render(RenderType.Cell)
              }
            }
          }
        }
      }
    }
  }
}
