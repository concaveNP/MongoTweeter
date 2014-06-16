/**
 * 
 */
package com.dave;

/**
 * @author dave
 * 
 */
public enum RelationalOperator {

	EQUAL("==", "$eq"), NOT_EQUAL("!=", "$ne"), GREATER_THAN(">", "$gt"), LESS_THAN(
			"<", "$lt"), GREATER_THAN_OR_EQUAL(">=", "$gte"), LESS_THAN_OR_EQUAL(
			"<=", "$lte"), UNKOWN("UNKOWN", "UNKOWN");

	private String myHuman;
	private String myMongo;

	private RelationalOperator(String human, String mongo) {
		this.myHuman = human;
		this.myMongo = mongo;
	}

	public static RelationalOperator fromHumanString(String human) {
		for (RelationalOperator operator : RelationalOperator.values()) {
			if (operator.myHuman.equalsIgnoreCase(human)) {
				return operator;
			}
		}

		return UNKOWN;
	}

	/**
	 * @return the Human form of the relational operator
	 */
	public String getHuman() {
		return myHuman;
	}

	/**
	 * @return the Mongo form of the relational operator
	 */
	public String getMongo() {
		return myMongo;
	}

}
