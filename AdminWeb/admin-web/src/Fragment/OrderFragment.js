import React, { Component } from "react";
import PropTypes from "prop-types";
import Box from "@material-ui/core/Box";
import Collapse from "@material-ui/core/Collapse";
import TableHead from "@material-ui/core/TableHead";
import Typography from "@material-ui/core/Typography";
import Paper from "@material-ui/core/Paper";
import KeyboardArrowDownIcon from "@material-ui/icons/KeyboardArrowDown";
import KeyboardArrowUpIcon from "@material-ui/icons/KeyboardArrowUp";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableFooter from "@material-ui/core/TableFooter";
import TablePagination from "@material-ui/core/TablePagination";
import TableRow from "@material-ui/core/TableRow";
import IconButton from "@material-ui/core/IconButton";
import FirstPageIcon from "@material-ui/icons/FirstPage";
import KeyboardArrowLeft from "@material-ui/icons/KeyboardArrowLeft";
import KeyboardArrowRight from "@material-ui/icons/KeyboardArrowRight";
import LastPageIcon from "@material-ui/icons/LastPage";
import {
  Avatar,
  Button,
  Container,
  Grid,
  TextField,
  Tooltip,
} from "@material-ui/core";
import { firestore } from "../firebase";
import { format } from "date-fns";
import ebay from "../ebayapi";
import OrderDetailDialog from "../Dialog/OrderDetailDialog";
const orderStatuses = [
  {
    value: "0",
    label: "",
    color: "#d6d6d6",
  },
  {
    value: "1",
    label: "Đã tạo",
    color: "#4BB543",
  },
  {
    value: "2",
    label: "Đã thanh toán",
    color: "#00C2C7",
  },
  {
    value: "3",
    label: "[USA]Đã lấy hàng/Đã nhập kho",
    color: "#82983B",
  },
  {
    value: "4",
    label: "[VN]Đã lấy hàng/Đã nhập kho",
    color: "#62F46E",
  },
  {
    value: "5",
    label: "[VN]Đã điều phối giao hàng/Đang giao hàng",
    color: "#44AA4D",
  },
  {
    value: "6",
    label: "Đã giao hàng",
    color: "#28662E",
  },
  {
    value: "7",
    label: "Đã hủy",
    color: "#D9092E",
  },
];

function SelectTextField(props) {
  //console.log(props.orderStatusValue);
  const [status, setStatus] = React.useState();
  const [backgroundColor, setBackgroundColor] = React.useState("0");

  const handleChange = (event) => {
    setStatus(event.target.value);
    setBackgroundColor(orderStatuses[event.target.value].color);
    props.orderStatus(
      orderStatuses[event.target.value].label,
      event.target.value
    );
  };

  return (
    <div>
      <TextField
        id="standard-select-currency-native"
        select
        value={status}
        style={{ backgroundColor: backgroundColor }}
        fullWidth
        onChange={handleChange}
        SelectProps={{
          native: true,
        }}
      >
        {orderStatuses.map((option) => (
          <option
            key={option.value}
            value={option.value}
            style={{ backgroundColor: option.color }}
          >
            {option.label}
          </option>
        ))}
      </TextField>
    </div>
  );
}
const useStyles1 = makeStyles((theme) => ({
  root: {
    flexShrink: 0,
    marginLeft: theme.spacing(2.5),
  },
}));

function TablePaginationActions(props) {
  const classes = useStyles1();
  const theme = useTheme();
  const { count, page, rowsPerPage, onChangePage } = props;

  const handleFirstPageButtonClick = (event) => {
    onChangePage(event, 0);
  };

  const handleBackButtonClick = (event) => {
    onChangePage(event, page - 1);
  };

  const handleNextButtonClick = (event) => {
    onChangePage(event, page + 1);
  };

  const handleLastPageButtonClick = (event) => {
    onChangePage(event, Math.max(0, Math.ceil(count / rowsPerPage) - 1));
  };

  return (
    <div className={classes.root}>
      <IconButton
        onClick={handleFirstPageButtonClick}
        disabled={page === 0}
        aria-label="first page"
      >
        {theme.direction === "rtl" ? <LastPageIcon /> : <FirstPageIcon />}
      </IconButton>
      <IconButton
        onClick={handleBackButtonClick}
        disabled={page === 0}
        aria-label="previous page"
      >
        {theme.direction === "rtl" ? (
          <KeyboardArrowRight />
        ) : (
          <KeyboardArrowLeft />
        )}
      </IconButton>
      <IconButton
        onClick={handleNextButtonClick}
        disabled={page >= Math.ceil(count / rowsPerPage) - 1}
        aria-label="next page"
      >
        {theme.direction === "rtl" ? (
          <KeyboardArrowLeft />
        ) : (
          <KeyboardArrowRight />
        )}
      </IconButton>
      <IconButton
        onClick={handleLastPageButtonClick}
        disabled={page >= Math.ceil(count / rowsPerPage) - 1}
        aria-label="last page"
      >
        {theme.direction === "rtl" ? <FirstPageIcon /> : <LastPageIcon />}
      </IconButton>
    </div>
  );
}

TablePaginationActions.propTypes = {
  count: PropTypes.number.isRequired,
  onChangePage: PropTypes.func.isRequired,
  page: PropTypes.number.isRequired,
  rowsPerPage: PropTypes.number.isRequired,
};
const useRowStyles = makeStyles({
  root: {
    "& > *": {
      borderBottom: "unset",
    },
  },
});

function createData(
  orderId,
  order_date,
  pay_date,
  pack_date,
  usa_ship_date,
  vn_ship_date,
  delivery_date,
  cancel_date,
  user,
  status,
  name,
  phone,
  address,
  totalAmount,
  deposit,
  totalItem,
  userId,
  product
) {
  return {
    orderId,
    order_date,
    pay_date,
    pack_date,
    usa_ship_date,
    vn_ship_date,
    delivery_date,
    cancel_date,
    user,
    status,
    name,
    phone,
    address,
    totalAmount,
    deposit,
    totalItem,
    userId,
    product,
  };
}

function Row(props) {
  const { row } = props;
  const [open, setOpen] = React.useState(false);
  const classes = useRowStyles();

  const getOrderDetailDialog = (row) => {
    props.showOrderDetail(true, row);
  };

  return (
    <React.Fragment>
      <TableRow className={classes.root}>
        <TableCell width="100">
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
        <TableCell width="150" onClick={() => getOrderDetailDialog(row)}>
          <Tooltip title="Xem chi tiết đơn hàng">
            <Button style={{ color: "blue" }}>{row.orderId}</Button>
          </Tooltip>
        </TableCell>
        <TableCell width="250">
          {format(row.order_date, "dd/MM/yyyy kk:mm:ss")}
        </TableCell>
        <TableCell width="400">{row.user}</TableCell>
        <TableCell align="right">${row.totalAmount}</TableCell>
        <TableCell align="right">${row.deposit}</TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box margin={1}>
              <Typography variant="h6" gutterBottom component="div">
                Sản phẩm
              </Typography>
              <Table size="small" aria-label="purchases">
                <TableHead>
                  <TableRow>
                    <TableCell width="400">Tên sản phẩm</TableCell>
                    <TableCell>Hình ảnh</TableCell>
                    <TableCell>Mô tả</TableCell>
                    <TableCell align="right">Khối lượng(kg)</TableCell>
                    <TableCell width="100" align="right">
                      Số lượng
                    </TableCell>
                    <TableCell width="200" align="right">
                      Giá tiền(bao gồm phụ thu)
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {row.product.map((productRow) => (
                    <TableRow key={productRow.title}>
                      <TableCell width="400">
                        <a href={productRow.link} target="_blank">
                          {productRow.title}
                        </a>
                      </TableCell>
                      <TableCell>
                        <Avatar
                          variant="square"
                          className={classes.large}
                          src={productRow.image}
                        ></Avatar>
                      </TableCell>
                      <TableCell>{productRow.description}</TableCell>
                      <TableCell align="right">{productRow.weight}</TableCell>
                      <TableCell width="100" align="right">
                        {productRow.quantity}
                      </TableCell>
                      <TableCell width="200" align="right">
                        ${productRow.price}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </React.Fragment>
  );
}

Row.propTypes = {
  row: PropTypes.shape({
    totalAmount: PropTypes.number.isRequired,
    deposit: PropTypes.number.isRequired,
    product: PropTypes.arrayOf(
      PropTypes.shape({
        title: PropTypes.string.isRequired,
        image: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        quantity: PropTypes.number.isRequired,
        price: PropTypes.number.isRequired,
        link: PropTypes.string.isRequired,
        weight: PropTypes.number.isRequired,
      })
    ).isRequired,
    orderId: PropTypes.string.isRequired,
    order_date: PropTypes.string.isRequired,
    user: PropTypes.string.isRequired,
  }).isRequired,
};

function CollapsibleTable(props) {
  const { rows } = props;
  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);

  const emptyRows =
    rowsPerPage - Math.min(rowsPerPage, rows.length - page * rowsPerPage);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };
  return (
    <TableContainer component={Paper}>
      <Table aria-label="collapsible table">
        <TableHead>
          <TableRow>
            <TableCell width="100" />
            <TableCell width="150">Mã đơn hàng</TableCell>
            <TableCell width="250">Thời gian tạo</TableCell>
            <TableCell width="400">Khách hàng</TableCell>
            <TableCell align="right">Tổng tiền hàng</TableCell>
            <TableCell align="right">Khách đã trả</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {(rowsPerPage > 0
            ? rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
            : rows
          ).map((row) => (
            <Row
              key={row.orderId}
              row={row}
              showOrderDetail={props.showOrderDetail}
            />
          ))}
          {emptyRows > 0 && (
            <TableRow style={{ height: 53 * emptyRows }}>
              <TableCell colSpan={6} />
            </TableRow>
          )}
        </TableBody>
        <TableFooter>
          <TableRow>
            <TablePagination
              rowsPerPageOptions={[5, 10, 25, { label: "All", value: -1 }]}
              colSpan={6}
              count={rows.length}
              rowsPerPage={rowsPerPage}
              page={page}
              SelectProps={{
                inputProps: { "aria-label": "rows per page" },
                native: true,
              }}
              onChangePage={handleChangePage}
              onChangeRowsPerPage={handleChangeRowsPerPage}
              ActionsComponent={TablePaginationActions}
            />
          </TableRow>
        </TableFooter>
      </Table>
    </TableContainer>
  );
}

class OrderFragment extends Component {
  constructor(props) {
    super(props);

    this.state = {
      rows: [],
      showOrderDetail: false,
      row: {},
      orderStatus: "",
      orderd: [],
      payed: [],
      packed: [],
      usa_shiped: [],
      vn_shiped: [],
      deliveried: [],
      cancel: [],
      visiblity1: false,
      visiblity2: false,
      visiblity3: false,
      visiblity4: false,
      visiblity5: false,
      visiblity6: false,
      visiblity7: false,
    };
  }

  componentDidMount() {
    this.getOrderDatabase();
  }

  getOrderDatabase = () => {
    firestore
      .collection("ORDERS")
      .get()
      .then((querySnapshot) => {
        let orders = [];
        let orderd = [];
        let payed = [];
        let packed = [];
        let usa_shiped = [];
        let vn_shiped = [];
        let deliveried = [];
        let cancel = [];
        querySnapshot.forEach((doc) => {
          firestore
            .collection("USERS")
            .doc(doc.get("user_ID"))
            .get()
            .then((docRef) => {
              let list_size = parseInt(doc.get("list_size"));
              let products = [];
              for (let i = 0; i < list_size; i++) {
                ebay.getAccessToken().then(() => {
                  let productId = doc.get("product_ID_" + i);
                  ebay.getItem(productId).then((data) => {
                    let product = {
                      id: i,
                      title: data.title,
                      image: data.image.imageUrl,
                      description: doc.get("product_des_" + i),
                      quantity: doc.get("product_quantity_" + i),
                      price: parseFloat(doc.get("product_price_" + i)),
                      link: data.itemWebUrl,
                      weight: parseFloat(doc.get("product_weight_" + i)),
                      transportfee: parseFloat(doc.get("transportfee_" + i)),
                      Id: productId,
                      categoryId: doc.get("category_ID_" + i),
                    };
                    products.push(product);
                  });
                });
              }

              let order = createData(
                doc.id,
                doc.get("ordered_date").toDate(),
                doc.get("payed_date").toDate(),
                doc.get("packed_date").toDate(),
                doc.get("shiped_usa_date").toDate(),
                doc.get("shiped_vn_date").toDate(),
                doc.get("delivery_date").toDate(),
                doc.get("cancelled_date").toDate(),
                docRef.get("fullname"),
                doc.get("order_status"),
                doc.get("fullname"),
                doc.get("phone_number"),
                doc.get("address"),
                Math.round(parseFloat(doc.get("total_amount")) * 100) / 100,
                parseFloat(doc.get("deposit")),
                parseInt(doc.get("total_item")),
                doc.get("user_ID"),
                products
              );
              if (order.status === "Đã tạo") {
                order.statusValue = 1;
                order.pay_date = "";
                order.pack_date = "";
                order.usa_ship_date = "";
                order.vn_ship_date = "";
                order.delivery_date = "";
                order.cancel_date = "";
                orderd.push(order);
              } else if (order.status === "Đã thanh toán") {
                order.statusValue = 2;
                order.pack_date = "";
                order.usa_ship_date = "";
                order.vn_ship_date = "";
                order.delivery_date = "";
                order.cancel_date = "";
                payed.push(order);
              } else if (order.status === "[USA]Đã lấy hàng/Đã nhập kho") {
                order.statusValue = 3;
                order.usa_ship_date = "";
                order.vn_ship_date = "";
                order.delivery_date = "";
                order.cancel_date = "";
                packed.push(order);
              } else if (order.status === "[VN]Đã lấy hàng/Đã nhập kho") {
                order.statusValue = 4;
                order.vn_ship_date = "";
                order.delivery_date = "";
                order.cancel_date = "";
                usa_shiped.push(order);
              } else if (
                order.status === "[VN]Đã điều phối giao hàng/Đang giao hàng"
              ) {
                order.statusValue = 5;
                order.delivery_date = "";
                order.cancel_date = "";
                vn_shiped.push(order);
              } else if (order.status === "Đã giao hàng") {
                order.statusValue = 6;
                order.cancel_date = "";
                deliveried.push(order);
              } else if (order.status === "Đã hủy") {
                order.statusValue = 7;
                cancel.push(order);
              }
              orders.push(order);
              this.setState({
                rows: orders,
              });
              this.setState({
                orderd: orderd,
                payed: payed,
                packed: packed,
                usa_shiped: usa_shiped,
                vn_shiped: vn_shiped,
                deliveried: deliveried,
                cancel: cancel,
              });
            });
        });
      });
  };

  setOrderStatus = (status, value) => {
    if (value === "1") {
      this.setState({
        visiblity1: true,
        visiblity2: false,
        visiblity3: false,
        visiblity4: false,
        visiblity5: false,
        visiblity6: false,
        visiblity7: false,
      });
    } else if (value === "2") {
      this.setState({
        visiblity1: false,
        visiblity2: true,
        visiblity3: false,
        visiblity4: false,
        visiblity5: false,
        visiblity6: false,
        visiblity7: false,
      });
    } else if (value === "3") {
      this.setState({
        visiblity1: false,
        visiblity2: false,
        visiblity3: true,
        visiblity4: false,
        visiblity5: false,
        visiblity6: false,
        visiblity7: false,
      });
    } else if (value === "4") {
      this.setState({
        visiblity1: false,
        visiblity2: false,
        visiblity3: false,
        visiblity4: true,
        visiblity5: false,
        visiblity6: false,
        visiblity7: false,
      });
    } else if (value === "5") {
      this.setState({
        visiblity1: false,
        visiblity2: false,
        visiblity3: false,
        visiblity4: false,
        visiblity5: true,
        visiblity6: false,
        visiblity7: false,
      });
    } else if (value === "6") {
      this.setState({
        visiblity1: false,
        visiblity2: false,
        visiblity3: false,
        visiblity4: false,
        visiblity5: false,
        visiblity6: true,
        visiblity7: false,
      });
    } else if (value === "7") {
      this.setState({
        visiblity1: false,
        visiblity2: false,
        visiblity3: false,
        visiblity4: false,
        visiblity5: false,
        visiblity6: false,
        visiblity7: true,
      });
    }
    this.setState({
      orderStatus: status,
    });
    //console.log(this.state.orderStatus);
  };

  setShowOrderDetail = (b, order) => {
    this.setState({
      showOrderDetail: b,
      row: order,
    });
  };
  render() {
    return (
      <div>
        {this.state.showOrderDetail ? (
          <OrderDetailDialog
            order={this.state.row}
            showOrderDetail={this.setShowOrderDetail}
            getData={this.getOrderDatabase}
          />
        ) : null}
        <Container maxWidth="lg">
          <Box
            bgcolor="white"
            boxShadow="3"
            borderRadius="2px"
            p="8px"
            mt="0px"
          >
            <Grid container spacing={3}>
              <Grid item xs={3}>
                <Typography variant="h6" gutterBottom>
                  Chọn tình trạng đơn hàng:
                </Typography>
              </Grid>
              <Grid item xs={9}>
                <SelectTextField orderStatus={this.setOrderStatus} />
              </Grid>
            </Grid>
          </Box>

          {this.state.visiblity1 ? (
            <Box
              bgcolor="white"
              boxShadow="3"
              borderRadius="2px"
              p="8px"
              mt="15px"
            >
              <Typography
                variant="h6"
                style={{
                  marginLeft: "20px",
                  marginTop: "20px",
                  color: "black",
                }}
              >
                {this.state.orderStatus}
              </Typography>
              <CollapsibleTable
                rows={this.state.orderd}
                showOrderDetail={this.setShowOrderDetail}
              />
            </Box>
          ) : null}
          {this.state.visiblity2 ? (
            <Box
              bgcolor="white"
              boxShadow="3"
              borderRadius="2px"
              p="8px"
              mt="15px"
            >
              <Typography
                variant="h6"
                style={{
                  marginLeft: "20px",
                  marginTop: "20px",
                  color: "black",
                }}
              >
                {this.state.orderStatus}
              </Typography>
              <CollapsibleTable
                rows={this.state.payed}
                showOrderDetail={this.setShowOrderDetail}
              />
            </Box>
          ) : null}
          {this.state.visiblity3 ? (
            <Box
              bgcolor="white"
              boxShadow="3"
              borderRadius="2px"
              p="8px"
              mt="15px"
            >
              <Typography
                variant="h6"
                style={{
                  marginLeft: "20px",
                  marginTop: "20px",
                  color: "black",
                }}
              >
                {this.state.orderStatus}
              </Typography>
              <CollapsibleTable
                rows={this.state.packed}
                showOrderDetail={this.setShowOrderDetail}
              />
            </Box>
          ) : null}
          {this.state.visiblity4 ? (
            <Box
              bgcolor="white"
              boxShadow="3"
              borderRadius="2px"
              p="8px"
              mt="15px"
            >
              <Typography
                variant="h6"
                style={{
                  marginLeft: "20px",
                  marginTop: "20px",
                  color: "black",
                }}
              >
                {this.state.orderStatus}
              </Typography>
              <CollapsibleTable
                rows={this.state.usa_shiped}
                showOrderDetail={this.setShowOrderDetail}
              />
            </Box>
          ) : null}
          {this.state.visiblity5 ? (
            <Box
              bgcolor="white"
              boxShadow="3"
              borderRadius="2px"
              p="8px"
              mt="15px"
            >
              <Typography
                variant="h6"
                style={{
                  marginLeft: "20px",
                  marginTop: "20px",
                  color: "black",
                }}
              >
                {this.state.orderStatus}
              </Typography>
              <CollapsibleTable
                rows={this.state.vn_shiped}
                showOrderDetail={this.setShowOrderDetail}
              />
            </Box>
          ) : null}
          {this.state.visiblity6 ? (
            <Box
              bgcolor="white"
              boxShadow="3"
              borderRadius="2px"
              p="8px"
              mt="15px"
            >
              <Typography
                variant="h6"
                style={{
                  marginLeft: "20px",
                  marginTop: "20px",
                  color: "black",
                }}
              >
                {this.state.orderStatus}
              </Typography>
              <CollapsibleTable
                rows={this.state.deliveried}
                showOrderDetail={this.setShowOrderDetail}
              />
            </Box>
          ) : null}
          {this.state.visiblity7 ? (
            <Box
              bgcolor="white"
              boxShadow="3"
              borderRadius="2px"
              p="8px"
              mt="15px"
            >
              <Typography
                variant="h6"
                style={{
                  marginLeft: "20px",
                  marginTop: "20px",
                  color: "black",
                }}
              >
                {this.state.orderStatus}
              </Typography>
              <CollapsibleTable
                rows={this.state.cancel}
                showOrderDetail={this.setShowOrderDetail}
              />
            </Box>
          ) : null}
        </Container>
      </div>
    );
  }
}

export default OrderFragment;
