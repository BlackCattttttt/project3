import React, { Component } from "react";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import Box from "@material-ui/core/Box";
import Collapse from "@material-ui/core/Collapse";
import IconButton from "@material-ui/core/IconButton";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Typography from "@material-ui/core/Typography";
import Paper from "@material-ui/core/Paper";
import KeyboardArrowDownIcon from "@material-ui/icons/KeyboardArrowDown";
import KeyboardArrowUpIcon from "@material-ui/icons/KeyboardArrowUp";
import { firestore } from "../firebase";
import { format } from "date-fns";
import {
  Button,
  Card,
  CardContent,
  Checkbox,
  Tooltip,
  withStyles,
} from "@material-ui/core";
import { green } from "@material-ui/core/colors";
import UserOrderDialog from "../Dialog/UserOrderDialog";

const useRowStyles = makeStyles({
  root: {
    "& > *": {
      borderBottom: "unset",
    },
  },
});
const GreenCheckbox = withStyles({
  root: {
    color: green[400],
    "&$checked": {
      color: green[600],
    },
    "&$disabled": {
      color: green[600],
    },
  },
  checked: {},
  disabled: {},
})(Checkbox);
function createData(index, email, name, createAt, count, address, orders) {
  return {
    index,
    email,
    name,
    createAt,
    count,
    address,
    orders,
  };
}

function Row(props) {
  const { row } = props;
  const [open, setOpen] = React.useState(false);
  const classes = useRowStyles();

  const getOrderlDialog = (row) => {
    props.showOrderDialog(true, row);
  };
  return (
    <React.Fragment>
      <TableRow className={classes.root}>
        <TableCell>
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
        <TableCell>{row.index}</TableCell>
        <TableCell align="left">{row.email}</TableCell>
        <TableCell align="left">{row.name}</TableCell>
        <TableCell align="left">
          {format(row.createAt, "dd/MM/yyyy kk:mm:ss")}
        </TableCell>
        <TableCell align="center" onClick={() => getOrderlDialog(row.orders)}>
          <Tooltip title="Xem danh sách đơn hàng">
            <Button style={{ color: "blue" }}>{row.count}</Button>
          </Tooltip>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box margin={1}>
              <Typography variant="h6" gutterBottom component="div">
                Sổ địa chỉ
              </Typography>
              <Table size="small" aria-label="purchases">
                <TableHead>
                  <TableRow>
                    <TableCell>Tên người nhận</TableCell>
                    <TableCell>Số điện thoại</TableCell>
                    <TableCell align="left">Địa chỉ</TableCell>
                    <TableCell align="center">Địa chỉ mặc định</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {row.address.map((addressRow) => (
                    <TableRow key={addressRow.fullname}>
                      <TableCell>{addressRow.fullname}</TableCell>
                      <TableCell>{addressRow.phone}</TableCell>
                      <TableCell align="left">{addressRow.address}</TableCell>
                      <TableCell align="center">
                        {" "}
                        <GreenCheckbox
                          defaultChecked={addressRow.select}
                          disabled
                          color="primary"
                          inputProps={{ "aria-label": "secondary checkbox" }}
                        />
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
    orders: PropTypes.number.isRequired,
    address: PropTypes.arrayOf(
      PropTypes.shape({
        fullname: PropTypes.string.isRequired,
        phone: PropTypes.number.isRequired,
        address: PropTypes.string.isRequired,
        select: PropTypes.bool.isRequired,
      })
    ).isRequired,
    name: PropTypes.string.isRequired,
    email: PropTypes.string.isRequired,
    createAt: PropTypes.string.isRequired,
  }).isRequired,
};

function CollapsibleTable(props) {
  const { rows } = props;
  return (
    <TableContainer component={Paper}>
      <Table aria-label="collapsible table">
        <TableHead>
          <TableRow>
            <TableCell />
            <TableCell>STT</TableCell>
            <TableCell align="left">Email</TableCell>
            <TableCell align="left">Tên khách hàng</TableCell>
            <TableCell align="left">Thời gian tạo</TableCell>
            <TableCell align="center">Số lượng đơn hàng đã tạo</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <Row
              key={row.name}
              row={row}
              showOrderDialog={props.showOrderDialog}
            />
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

export class UserFragment extends Component {
  constructor(props) {
    super(props);

    this.state = {
      rows: [],
      order: [],
      showOrderDialog: false,
    };
  }

  componentDidMount() {
    this.getUserDatabase();
  }

  getUserDatabase = () => {
    firestore
      .collection("USERS")
      .where("email", "!=", "admin@gmail.com")
      .get()
      .then((querySnapshot) => {
        let index = 0;
        let users = [];
        querySnapshot.forEach((doc) => {
          let count = 0;
          firestore
            .collection("USERS")
            .doc(doc.id)
            .collection("USER_ORDER")
            .get()
            .then((snapshot) => {
              count = snapshot.size;
              let orders = [];
              snapshot.forEach((document) => {
                orders.push(document.get("order_id"));
              });
              firestore
                .collection("USERS")
                .doc(doc.id)
                .collection("USER_DATA")
                .doc("MY_ADDRESSES")
                .get()
                .then((docRef) => {
                  index++;
                  let size = docRef.get("list_size");
                  let addresses = [];
                  for (let i = 1; i <= size; i++) {
                    let ad = {
                      address: docRef.get("address_full_" + i),
                      fullname: docRef.get("fullname_" + i),
                      phone: docRef.get("phonenumber_" + i),
                      select: docRef.get("selected_" + i),
                    };
                    addresses.push(ad);
                  }
                  let user = createData(
                    index,
                    doc.get("email"),
                    doc.get("fullname"),
                    doc.get("create_at").toDate(),
                    count,
                    addresses,
                    orders
                  );
                  users.push(user);
                  this.setState({
                    rows: users,
                  });
                });
            });
        });
      });
  };

  setShowUserOrderDialog = (b, order) => {
    this.setState({
      showOrderDialog: b,
      order: order,
    });
  };
  render() {
    return (
      <div>
        {this.state.showOrderDialog ? (
          <UserOrderDialog
            orders={this.state.order}
            showOrderDialog={this.setShowUserOrderDialog}
          />
        ) : null}
        <Card>
          <CardContent>
            <Typography variant="h4">Danh sách người dùng</Typography>
            <CollapsibleTable
              rows={this.state.rows}
              showOrderDialog={this.setShowUserOrderDialog}
            />
          </CardContent>
        </Card>
      </div>
    );
  }
}

export default UserFragment;
