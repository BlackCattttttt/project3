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
import { Avatar, Button, Container, Grid } from "@material-ui/core";
import { firestore } from "../firebase";
import { format } from "date-fns";
import ebay from "../ebayapi";

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
  status,
  totalAmount,
  deposit,
  product
) {
  return {
    orderId,
    order_date,
    status,
    totalAmount,
    deposit,
    product,
  };
}

function Row(props) {
  const { row } = props;
  const [open, setOpen] = React.useState(false);
  const classes = useRowStyles();

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
        <TableCell width="150">{row.orderId}</TableCell>
        <TableCell width="250">
          {format(row.order_date, "dd/MM/yyyy kk:mm:ss")}
        </TableCell>
        <TableCell width="400">{row.status}</TableCell>
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
            <TableCell width="400">Trạng thái</TableCell>
            <TableCell align="right">Tổng tiền hàng</TableCell>
            <TableCell align="right">Khách đã trả</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {(rowsPerPage > 0
            ? rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
            : rows
          ).map((row) => (
            <Row key={row.orderId} row={row} />
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
export class UserOrderDialog extends Component {
  constructor(props) {
    super(props);

    this.state = {
      rows: [],
      orders: this.props.orders,
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
        querySnapshot.forEach((doc) => {
          for (let x = 0; x < this.state.orders.length; x++) {
            if (doc.id === this.state.orders[x]) {
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
                doc.get("order_status"),
                Math.round(parseFloat(doc.get("total_amount")) * 100) / 100,
                parseFloat(doc.get("deposit")),
                products
              );
              orders.push(order);
              this.setState({
                rows: orders,
              });
            }
          }
        });
      });
  };
  onCancel() {
    this.props.showOrderDialog(false);
  }
  render() {
    return (
      <div
        style={{
          width: "100vw",
          height: "100vh",
          backgroundColor: "rgb(0, 0, 0, 0.4)",
          zIndex: "100",
          position: "absolute",
          top: 0,
          left: 0,
          marginLeft: "100px",
          marginTop: "60px",
        }}
      >
        <Container Container maxWidth="lg">
          <Box
            bgcolor="white"
            boxShadow="3"
            borderRadius="2px"
            p="8px"
            mt="15px"
          >
            <Typography variant="h4">Danh sách đơn hàng</Typography>
            <CollapsibleTable rows={this.state.rows} />
            <Grid container spacing={3} style={{ marginTop: "15px" }}>
              <Grid item xs={4}></Grid>
              <Grid item xs={4}>
                <Button
                  variant="outlined"
                  fullWidth
                  onClick={() => this.onCancel()}
                >
                  Hủy
                </Button>
              </Grid>
              <Grid item xs={4}></Grid>
            </Grid>
          </Box>
        </Container>
      </div>
    );
  }
}

export default UserOrderDialog;
