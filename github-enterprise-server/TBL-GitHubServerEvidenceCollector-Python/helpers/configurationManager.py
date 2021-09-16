import configparser

# stores the global configuration manager to ensure only 1 is created
__config = None

def get_configuration_manager():
    """This method generates the configuration manager which retrieves the configuration settings from the config.ini file"""
    global __config
    if __config is None:
        __config = configparser.ConfigParser()
        __config.read('config.ini')
    return __config