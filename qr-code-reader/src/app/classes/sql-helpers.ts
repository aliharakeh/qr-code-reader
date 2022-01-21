function createTableQuery(table: string, columnsWithTypes: string[]) {
  const columns = columnsWithTypes.join(',');
  return `create table ${table}(${columns});`;
}

function dropTableQuery(table: string) {
  return `drop table ${table};`;
}

function selectQuery(table: string, columns: string[], extra: string) {
  let joinedColumns = columns.join(',');
  return `select ${joinedColumns} from ${table} ${extra};`;
}

function insertQuery(table: string, columns: string[]) {
  let joinedColumns = columns.join(',');
  let values = columns.map(c => '?').join(',');
  return `insert into ${table}(${joinedColumns}) values (${values});`;
}

function deleteQuery(table: string, columns: string[]) {
  let condition = '1';
  if (columns && columns.length > 0) {
    condition = columns.reduce((res, v, i) => {
      if (i > 0) {
        res += ' ';
      }
      res += v + ' = ?';
      return res;
    }, '');
  }
  return `delete from ${table} where ${condition};`;
}

export {
  createTableQuery,
  dropTableQuery,
  selectQuery,
  insertQuery,
  deleteQuery
}
