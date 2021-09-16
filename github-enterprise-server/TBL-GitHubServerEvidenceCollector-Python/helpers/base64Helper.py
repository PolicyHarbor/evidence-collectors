import base64

def to_base_64_string(value: str):
    """This method helps converting a string value into a base-64 encoded string

    Params
    ------
    value : str
    The value to be converted into base-64 encoded string"""
    # encode str into a 8-bit variable
    var_8bit = value.encode("UTF-8")
    # encode into base 64 bytes
    b64bytes = base64.b64encode(var_8bit)
    # return the string representation of the base-64 bytes
    return b64bytes.decode("UTF-8")