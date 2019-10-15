from setuptools import setup

setup(
    name='import_data_to_kafka',
    version='1.0',
    py_modules=['import_data_to_kafka'],
    include_package_data=True,
    install_requires=[
        'click',
        # Colorama is only required for Windows.
        'kafka-python',
        'gssapi'
    ],
    entry_points='''
        [console_scripts]
        import_data_to_kafka=import_data_to_kafka:run
    ''',
)
